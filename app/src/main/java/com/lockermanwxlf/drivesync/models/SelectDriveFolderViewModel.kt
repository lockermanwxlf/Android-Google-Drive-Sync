package com.lockermanwxlf.drivesync.models

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.lockermanwxlf.drive.DriveClient
import com.lockermanwxlf.drive.DriveFolder
import com.lockermanwxlf.drivesync.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SelectDriveFolderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
): ViewModel() {
    private var driveClient: DriveClient? = null
    var driveFolders: List<DriveFolder> by mutableStateOf(listOf())
        private set
    fun setAccount(account: GoogleSignInAccount?) {
        driveClient = account?.let { DriveClient(context, account.account!!, MainActivity.SCOPES) }
        if (driveClient == null) {
            driveFolders = listOf()
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                driveFolders = driveClient!!.getAllFolders()
            }
        }
    }
}