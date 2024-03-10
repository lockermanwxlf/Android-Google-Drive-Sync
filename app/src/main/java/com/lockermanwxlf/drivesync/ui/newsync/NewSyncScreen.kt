package com.lockermanwxlf.drivesync.ui.newsync

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.provider.DocumentsContractCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lockermanwxlf.drivesync.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.lockermanwxlf.drive.DriveFolder
import com.lockermanwxlf.drivesync.models.NewSyncViewModel
import com.lockermanwxlf.drivesync.utils.data.ChecksumMismatchBehavior
import com.lockermanwxlf.drivesync.utils.data.DeleteOn
import com.lockermanwxlf.drivesync.utils.data.SyncType

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSyncScreen(navController: NavController) {
    val vm: NewSyncViewModel = hiltViewModel()
    val context = LocalContext.current
    val account by remember {
        mutableStateOf(GoogleSignIn.getLastSignedInAccount(context))
    }
    if (account == null) {
        navController.popBackStack()
        return
    }

    val deviceUri by vm.deviceUri.collectAsState()
    val deviceName by vm.deviceName.collectAsState()
    val driveId by vm.driveId.collectAsState()
    val driveName by vm.driveName.collectAsState()
    val syncType by vm.syncType.collectAsState()
    val checksumMismatchBehavior by vm.checksumMismatchBehavior.collectAsState()
    val deleteOn by vm.deleteOn.collectAsState()

    val selectedFolder by navController.currentBackStackEntry
        ?.savedStateHandle!!
        .getStateFlow<DriveFolder?>("selected_folder", null)
        .collectAsState()
    vm.setDriveId(selectedFolder?.id)
    vm.setDriveName(selectedFolder?.name)

    val enabled = deviceUri != null && deviceName != null && driveId != null && driveName != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Sync") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack("Home", false)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Back to home")
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(visible = enabled) {
                TextButton(
                    onClick = {
                        vm.addSync(account!!.id!!)
                        navController.popBackStack()
                    },
                    enabled = enabled
                ) {
                    Text("Create sync")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DeviceFolderPicker(
                uri = deviceUri,
                folderName = deviceName,
                onUriChanged = {
                    if (it != null) {
                        vm.setDeviceUri(it)
                        context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        val uri = DocumentsContractCompat.buildDocumentUriUsingTree(it, DocumentsContractCompat.getTreeDocumentId(it)!!)
                        context.contentResolver.query(uri!!, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME), null, null)?.use { cursor ->
                            cursor.moveToFirst()
                            val index = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                            vm.setDeviceName(cursor.getString(index))
                        }
                    } else {
                        vm.setDeviceUri(null)
                        vm.setDeviceName(null)
                    }
                }
            )
            Divider()
            TextButton(onClick = { navController.navigate("Select Drive Folder") }) {
                Text(selectedFolder?.name ?: "Select drive folder")
            }
            Divider()
            SyncTypePicker(
                syncType = syncType,
                onSyncTypeChanged = {
                    vm.setSyncType(it)
                    vm.setChecksumBehavior(when(it) {
                        SyncType.BOTH -> ChecksumMismatchBehavior.DOWNLOAD
                        SyncType.DOWNLOAD -> ChecksumMismatchBehavior.IGNORE
                        SyncType.UPLOAD -> ChecksumMismatchBehavior.UPLOAD
                    })
                    vm.setDeleteOn(DeleteOn.NEVER)
                }
            )
            Divider()
            MismatchBehaviorPicker(
                checksumMismatchBehavior = checksumMismatchBehavior,
                syncType = syncType,
                onChange = {
                    vm.setChecksumBehavior(it)
                }
            )
            Divider()
            DeleteOnPicker(
                deleteOn = deleteOn,
                syncType = syncType,
                onChange = {
                    vm.setDeleteOn(it)
                }
            )
        }
    }
}