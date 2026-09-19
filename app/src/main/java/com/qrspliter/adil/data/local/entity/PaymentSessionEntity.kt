package com.qrspliter.adil.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_sessions",
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"]),
        Index(value = ["referenceId"])
    ]
)
data class PaymentSessionEntity(
    @PrimaryKey
    val sessionId: String,
    val totalAmountPaise: Long,
    val merchantVpa: String,
    val merchantName: String,
    val referenceId: String,
    val note: String?,
    val status: String,
    val createdAt: Long
)
