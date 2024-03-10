package com.lockermanwxlf.drive

import android.accounts.Account
import android.content.Context
import android.net.Uri
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class DriveClient(
    context: Context,
    account: Account,
    scopes: List<String>
) {
    companion object {
        private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        private val jsonFactory: GsonFactory = GsonFactory.getDefaultInstance()
    }
    private val context = context.applicationContext
    private val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(context, scopes)
        .setSelectedAccount(account)
    private val service: Drive = Drive.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("Drive Sync")
        .build()

    suspend fun getAllFolders() = withContext(Dispatchers.IO) {
        val foldersMap = mutableMapOf<String, DriveFolder>()
        val parents = mutableMapOf<String, String>()
        val request = service.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder'")
            .setFields("files(mimeType,id,parents,name,ownedByMe)")
        val files = request.execute().files.filter {
            it.ownedByMe
        }
        files.forEach {
            foldersMap[it.id] = DriveFolder(it.id, it.name, null, listOf())
            if (it.parents != null && it.parents.size > 0) {
                parents[it.id] = it.parents.first()
            }
        }
        parents.forEach {
            foldersMap[it.key] = foldersMap[it.key]!!.copy(parent = foldersMap[it.value])
            if (foldersMap[it.value] != null) {
                foldersMap[it.value] = foldersMap[it.value]!!.copy(
                    subfolders = (foldersMap[it.value]!!.subfolders + foldersMap[it.key]!!).sortedBy { it.name }
                )
            }
        }
        return@withContext foldersMap.values.filter { it.parent == null }.toList().sortedBy { it.name }
    }
    suspend fun getFolderContents(folderId: String) = withContext(Dispatchers.IO) {
        val request = service.files().list()
            .setQ("'$folderId' in parents")
            .setFields("files(id,name,parents,mimeType,md5Checksum)")
        val files = request.execute().files
        return@withContext files.map {
            DriveFile(it.id, it.name, it.mimeType, it.md5Checksum, service)
        }
    }
    suspend fun uploadFile(filename: String, documentUri: Uri, length: Long, mimeType: String, parentId: String) = withContext(Dispatchers.IO) {
        val file = File().apply {
            name = filename
            parents = listOf(parentId)
            setMimeType(mimeType)
        }
        val fileContent = SyncFileContent(mimeType, documentUri, length, context.contentResolver)
        service.files().create(file, fileContent)
            .execute()
    }
}