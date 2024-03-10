package com.lockermanwxlf.drivesync.utils.data

import android.content.Context
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.internal.synchronized
import java.time.ZoneId
import java.time.temporal.TemporalAccessor
import java.util.GregorianCalendar
import java.util.TimeZone
import javax.inject.Singleton

@Database(
    entities = [Sync::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(SyncTypeConverters::class)
abstract class SyncDatabase: RoomDatabase() {
    abstract fun getSyncDao(): SyncDao

    companion object {
        @Volatile
        private var _instance: SyncDatabase? = null
        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(context: Context) = _instance ?: synchronized(this) {
            Room.databaseBuilder(context = context.applicationContext, SyncDatabase::class.java, "sync-database")
                .build().also { _instance = it }
        }
    }
}