package com.lockermanwxlf.drivesync.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.InputStream

class ListenableInputStream(
    private val length: Long,
    private val inputStream: InputStream
): InputStream() {
    var bytesRead = 0L
    private val _progress = MutableStateFlow(0F)
    val progress = _progress.asStateFlow()

    override fun read(): Int {
        bytesRead += 1
        _progress.value = bytesRead.toFloat()/length
        return inputStream.read()
    }
}