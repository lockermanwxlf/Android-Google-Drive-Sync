package com.lockermanwxlf.drive

import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

data class DriveFile internal constructor(
    val driveId: String,
    val name: String,
    val mimeType: String,
    val md5Checksum: String,
    val driveService: Drive
)  {
    suspend fun downloadToOutputStream(outputStream: OutputStream) = withContext(Dispatchers.IO) {
        driveService.files().get(driveId).executeMediaAndDownloadTo(outputStream)
    }
}