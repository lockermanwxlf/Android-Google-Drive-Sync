package com.lockermanwxlf.drivesync.utils.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SyncDao {
    @Query("SELECT * FROM sync WHERE googleAccountId=:accountId")
    abstract fun getSyncFlow(accountId: String): Flow<List<Sync>>

    @Query("SELECT * FROM sync WHERE googleAccountId=:accountId")
    abstract suspend fun getSyncs(accountId: String): List<Sync>

    @Update
    abstract suspend fun updateSync(sync: Sync)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg sync: Sync)

    @Delete
    abstract suspend fun delete(sync: Sync)
}