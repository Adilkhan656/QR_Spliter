package com.qrspliter.adil.domain.repository

import com.qrspliter.adil.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeAllNotifications(): Flow<List<AppNotification>>
    fun observeUnreadCount(): Flow<Int>
    suspend fun addNotification(title: String, message: String)
    suspend fun markAllNotificationsRead()
    suspend fun clearAllNotifications()
}
