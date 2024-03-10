package com.lockermanwxlf.drivesync.ui.newsync

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.provider.DocumentsContractCompat

@Composable
fun DeviceFolderPicker(
    uri: Uri?,
    folderName: String?,
    onUriChanged: (Uri?)->Unit
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
            onUriChanged(it?:uri)
        }
    TextButton(
        onClick = {
            launcher.launch(uri)
        }
    ) {
        Text(text = folderName?:"Select Device Folder")
    }
}