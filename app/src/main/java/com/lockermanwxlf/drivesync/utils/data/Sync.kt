package com.lockermanwxlf.drivesync.utils.data

import android.net.Uri
import androidx.room.Entity

@Entity(primaryKeys = ["deviceUri", "driveId"])
data class Sync(
    val deviceUri: Uri,
    val deviceName: String,
    val driveId: String,
    val driveName: String,
    val googleAccountId: String,
    val syncType: SyncType,
    val checksumMismatchBehavior: ChecksumMismatchBehavior,
    val deleteOn: DeleteOn,
    val timeCreated: Long = System.currentTimeMillis()
)