package com.lockermanwxlf.drivesync.utils.extensions

import java.io.InputStream
import java.security.MessageDigest

fun InputStream.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return use { fis ->
        val buffer = ByteArray(8192)
        generateSequence {
            when (val bytesRead = fis.read(buffer)) {
                -1 -> null
                else -> bytesRead
            }
        }.forEach { bytesRead -> md.update(buffer, 0, bytesRead) }
        md.digest().joinToString("") { "%02x".format(it) }
    }
}