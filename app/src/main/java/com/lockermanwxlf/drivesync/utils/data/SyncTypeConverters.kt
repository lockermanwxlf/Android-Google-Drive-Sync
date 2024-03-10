package com.lockermanwxlf.drivesync.utils.data

import android.net.Uri
import androidx.room.TypeConverter

class SyncTypeConverters {
    @TypeConverter
    fun stringToUri(string: String) = Uri.parse(string)

    @TypeConverter
    fun uriToString(uri: Uri) = uri.toString()

    @TypeConverter
    fun syncTypeToInt(syncType: SyncType) = syncType.ordinal

    @TypeConverter
    fun intToSyncType(int: Int) = SyncType.entries[int]

    @TypeConverter
    fun checksumMismatchBehaviorToInt(checksumMismatchBehavior: ChecksumMismatchBehavior) = checksumMismatchBehavior.ordinal

    @TypeConverter
    fun intToChecksumMismatchBehavior(int: Int) = ChecksumMismatchBehavior.entries[int]

    @TypeConverter
    fun deleteOnToInt(deleteOn: DeleteOn) = deleteOn.ordinal

    @TypeConverter
    fun intToDeleteOn(int: Int) = DeleteOn.entries[int]
}