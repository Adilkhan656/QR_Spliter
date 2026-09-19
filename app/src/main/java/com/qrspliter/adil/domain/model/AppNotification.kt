package com.qrspliter.adil.domain.model

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
