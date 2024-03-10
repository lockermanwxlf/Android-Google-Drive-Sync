package com.lockermanwxlf.drivesync.utils.data

enum class ChecksumMismatchBehavior(val displayName: String) {
    IGNORE("Ignore"),
    DOWNLOAD("Download"),
    UPLOAD("Upload")
}