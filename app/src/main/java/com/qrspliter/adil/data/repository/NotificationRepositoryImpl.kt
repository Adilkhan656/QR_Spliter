package com.qrspliter.adil.data.repository

import com.qrspliter.adil.data.local.dao.NotificationDao
import com.qrspliter.adil.data.local.entity.NotificationEntity
import com.qrspliter.adil.domain.model.AppNotification
import com.qrspliter.adil.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class NotificationRepositoryImpl(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun observeAllNotifications(): Flow<List<AppNotification>> {
        return notificationDao.observeAllNotifications().map { entities ->
            entities.map {
                AppNotification(
                    id = it.id,
                    title = it.title,
                    message = it.message,
                    timestamp = it.timestamp,
                    isRead = it.isRead
                )
            }
        }
    }

    override fun observeUnreadCount(): Flow<Int> {
        return notificationDao.observeUnreadCount()
    }

    override suspend fun addNotification(title: String, message: String) {
        val entity = NotificationEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun markAllNotificationsRead() {
        notificationDao.markAllNotificationsRead()
    }

    override suspend fun clearAllNotifications() {
        notificationDao.clearAllNotifications()
    }
}
