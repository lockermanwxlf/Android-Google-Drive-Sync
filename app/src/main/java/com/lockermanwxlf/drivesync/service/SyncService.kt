package com.lockermanwxlf.drivesync.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.net.Uri
import android.os.IBinder
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.provider.DocumentsContractCompat
import androidx.documentfile.provider.DocumentFile
import com.lockermanwxlf.drivesync.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.lockermanwxlf.drive.DriveClient
import com.lockermanwxlf.drivesync.MainActivity
import com.lockermanwxlf.drivesync.utils.data.ChecksumMismatchBehavior
import com.lockermanwxlf.drivesync.utils.data.DeleteOn
import com.lockermanwxlf.drivesync.utils.data.SyncDao
import com.lockermanwxlf.drivesync.utils.data.SyncDatabase
import com.lockermanwxlf.drivesync.utils.data.SyncType
import com.lockermanwxlf.drivesync.utils.extensions.md5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject


class SyncService: Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var syncDao: SyncDao
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    fun makeNotification(msg: String): Notification {

        val channel = NotificationChannelCompat.Builder("12345", NotificationManager.IMPORTANCE_LOW)
            .setName("Drive Sync Service")
            .setLightColor(Color.BLUE)
            .setVibrationEnabled(false)
            .setDescription("Drive Sync Foreground Service Notification")
            .build()
        notificationManager.createNotificationChannel(channel)
        return NotificationCompat.Builder(applicationContext, channel.id)
            .setContentTitle("Drive Sync")
            .setContentText(msg)
            .setSmallIcon(R.drawable.baseline_cloud_download_24)
            .build()
    }

    fun setNotificationText(msg: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(12124725, makeNotification(msg))
        }
    }

    suspend fun servicePass() = withContext(Dispatchers.IO) {

        //make less ugly in future.

        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)

        if (account == null) {
            setNotificationText("Log in to start syncing.")
            return@withContext
        } else {
            setNotificationText("Syncing")
        }

        val client = DriveClient(applicationContext, account.account!!, MainActivity.SCOPES)
        val syncs = syncDao.getSyncs(account.id!!)
        syncs.forEach { sync ->
            setNotificationText("Syncing ${sync.deviceName}.")
            val treeUri = DocumentsContractCompat.buildChildDocumentsUriUsingTree(
                sync.deviceUri,
                DocumentsContractCompat.getTreeDocumentId(sync.deviceUri)!!
            )!!
            val downloadFIles = client.getFolderContents(sync.driveId).toMutableList()
            contentResolver.query(
                treeUri,
                arrayOf(
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    DocumentsContract.Document.COLUMN_SIZE
                ),
                null,
                null
            )?.use {
                val index = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val idIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val mimeIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
                while (it.moveToNext()) {
                    val displayName = it.getString(index)
                    val size = it.getLong(sizeIndex)
                    val id = it.getString(idIndex)
                    val mimeType = it.getString(mimeIndex)
                    if (it.getString(mimeIndex) != DocumentsContract.Document.MIME_TYPE_DIR) {
                        val fileUri = DocumentsContractCompat.buildDocumentUriUsingTree(sync.deviceUri, id)
                        val sum = contentResolver.openInputStream(fileUri!!)?.use {
                            it.md5()
                        }

                        /* by default, all drives files are to be downloaded
                           if a file is present on device
                           1) checksums equal -> remove from list
                           2) checksums different ->
                            1) ignore -> ignore
                            2) download -> ignore
                            3) upload -> upload
                           if file not present
                            1) upload -> upload
                            2) not upload -> ignore
                        */
                        var upload = false
                        var download = false
                        var delete = false
                        val fileInDrive = downloadFIles.find { it.name == displayName }
                        if (fileInDrive == null) {
                            if (sync.syncType == SyncType.BOTH || sync.syncType == SyncType.UPLOAD) {
                                upload = true
                            }
                        } else { //file is in drive
                            if (fileInDrive.md5Checksum != sum) {
                                upload = sync.checksumMismatchBehavior == ChecksumMismatchBehavior.UPLOAD
                                download = sync.checksumMismatchBehavior == ChecksumMismatchBehavior.DOWNLOAD
                            }
                            if (sync.deleteOn == DeleteOn.ON_PRESENT) {
                                delete = true
                            }
                            downloadFIles.remove(fileInDrive)
                        }


                        if (upload) {
                            setNotificationText("Uploading $displayName.")
                            client.uploadFile(
                                displayName,
                                fileUri,
                                size,
                                mimeType,
                                sync.driveId
                            )
                            if (sync.deleteOn == DeleteOn.ON_UPLOAD) {
                                delete = true
                            }
                            setNotificationText("Uploaded $displayName.")



                        } else if (download) {
                            setNotificationText("Fixing $displayName.")
                            contentResolver.openOutputStream(fileUri)?.use { fos ->
                                fileInDrive?.downloadToOutputStream(fos)
                            }
                            setNotificationText("Fixed $displayName.")
                        }

                        if (delete) {
                            setNotificationText("Deleting $displayName from device.")
                            DocumentsContract.deleteDocument(contentResolver, fileUri)
                            setNotificationText("Deleted $displayName from device.")
                        }

                    }
                }
            }
            if (sync.syncType == SyncType.BOTH || sync.syncType == SyncType.DOWNLOAD) {
                downloadFIles.forEach {
                    DocumentsContractCompat.createDocument(
                        contentResolver,
                        treeUri,
                        it.mimeType,
                        it.name
                    )?.let { uri ->
                        val fileUri = DocumentsContractCompat.buildDocumentUriUsingTree(
                            sync.deviceUri,
                            DocumentsContractCompat.getDocumentId(uri)!!
                        )!!
                        setNotificationText("Downloading ${it.name}.")
                        contentResolver.openOutputStream(fileUri)?.use { fos ->
                            it.downloadToOutputStream(fos)
                        }
                        setNotificationText("Downloaded ${it.name}.")
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        syncDao = SyncDatabase.getInstance(applicationContext).getSyncDao()
        notificationManager = NotificationManagerCompat.from(applicationContext)
        startForeground(12124725, makeNotification("Service launched."), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)

        scope.launch {
            while (true) {
                servicePass()
                Thread.sleep(7000)
            }
        }
    }
}