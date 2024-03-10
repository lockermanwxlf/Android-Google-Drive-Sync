package com.lockermanwxlf.drivesync.models

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.lockermanwxlf.drive.DriveClient
import com.lockermanwxlf.drive.DriveFolder
import com.lockermanwxlf.drivesync.MainActivity
import com.lockermanwxlf.drivesync.utils.data.ChecksumMismatchBehavior
import com.lockermanwxlf.drivesync.utils.data.DeleteOn
import com.lockermanwxlf.drivesync.utils.data.Sync
import com.lockermanwxlf.drivesync.utils.data.SyncDao
import com.lockermanwxlf.drivesync.utils.data.SyncType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class NewSyncViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val syncDao: SyncDao
): ViewModel() {
    var driveService: DriveClient? = null
    var folders by mutableStateOf(listOf<DriveFolder>())
        private set

    val deviceUri = savedStateHandle.getStateFlow<Uri?>("deviceUri", null)
    val deviceName = savedStateHandle.getStateFlow<String?>("deviceName", null)
    val driveId = savedStateHandle.getStateFlow<String?>("driveId", null)
    val driveName = savedStateHandle.getStateFlow<String?>("driveName", null)
    val syncType = savedStateHandle.getStateFlow<SyncType>("syncType", SyncType.BOTH)
    val checksumMismatchBehavior = savedStateHandle.getStateFlow<ChecksumMismatchBehavior>("checksumMismatchBehavior", ChecksumMismatchBehavior.DOWNLOAD)
    val deleteOn = savedStateHandle.getStateFlow<DeleteOn>("deleteOn", DeleteOn.NEVER)

    fun setDeviceUri(deviceUri: Uri?) {
        savedStateHandle["deviceUri"] = deviceUri
    }
    fun setDeviceName(deviceName: String?) {
        savedStateHandle["deviceName"] = deviceName
    }
    fun setDriveId(driveId: String?) {
        savedStateHandle["driveId"] = driveId
    }
    fun setDriveName(driveName: String?) {
        savedStateHandle["driveName"] = driveName
    }
    fun setSyncType(syncType: SyncType) {
        savedStateHandle["syncType"] = syncType
    }
    fun setChecksumBehavior(checksumMismatchBehavior: ChecksumMismatchBehavior) {
        savedStateHandle["checksumMismatchBehavior"] = checksumMismatchBehavior
    }
    fun setDeleteOn(deleteOn: DeleteOn) {
        savedStateHandle["deleteOn"] = deleteOn
    }


    fun addSync(googleAccountId: String) = viewModelScope.launch(Dispatchers.IO) {
        if (deviceUri.value != null && deviceName.value != null && driveId.value != null && driveName.value != null) {
            syncDao.insert(Sync(
                deviceUri.value!!,
                deviceName.value!!,
                driveId.value!!,
                driveName.value!!,
                googleAccountId,
                syncType.value,
                checksumMismatchBehavior.value,
                deleteOn.value
            ))
        }
    }

    fun setAccount(account: GoogleSignInAccount) = viewModelScope.launch(Dispatchers.IO) {
        driveService = DriveClient(context, account.account!!, MainActivity.SCOPES)
        folders = driveService!!.getAllFolders()
    }

}