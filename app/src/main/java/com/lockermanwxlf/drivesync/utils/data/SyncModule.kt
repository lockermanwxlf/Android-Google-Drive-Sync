package com.lockermanwxlf.drivesync.utils.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SyncModule {
    @Provides
    @Singleton
    fun getSyncDatabase(@ApplicationContext context: Context) = SyncDatabase.getInstance(context)

    @Provides
    @Singleton
    fun getSyncDao(syncDatabase: SyncDatabase) = syncDatabase.getSyncDao()
}