package com.qrspliter.adil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qrspliter.adil.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun observeAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun observeUnreadCount(): Flow<Int>

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsRead(): Int

    @Query("DELETE FROM app_notifications")
    suspend fun clearAllNotifications(): Int
}
