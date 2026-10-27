package com.devpupu.notifgrouper.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.devpupu.notifgrouper.data.db.AppDatabase
import com.devpupu.notifgrouper.data.repository.GroupingEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "devpupu.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGroupedAppDao(db: AppDatabase) = db.groupedAppDao()

    @Provides
    fun provideNotificationHistoryDao(db: AppDatabase) = db.notificationHistoryDao()

    @Provides
    @Singleton
    fun provideGroupingEngine(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager,
        historyDao: com.devpupu.notifgrouper.data.db.NotificationHistoryDao,
    ): GroupingEngine = GroupingEngine(context, notificationManager, historyDao)

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        ContextCompat.getSystemService(context, NotificationManager::class.java)!!
}
