package com.lockermanwxlf.drive

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.provider.DocumentsContractCompat
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent
import java.io.InputStream

class SyncFileContent(
    mimeType: String,
    private val documentUri: Uri,
    private val length: Long,
    private val contentResolver: ContentResolver
): AbstractInputStreamContent(mimeType) {
    override fun getLength(): Long {
        return length
    }

    override fun retrySupported(): Boolean {
        return true
    }

    override fun getInputStream(): InputStream {
        return contentResolver.openInputStream(documentUri)!!
    }
}