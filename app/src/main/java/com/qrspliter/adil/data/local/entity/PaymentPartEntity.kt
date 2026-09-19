package com.qrspliter.adil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_parts",
    foreignKeys = [
        ForeignKey(
            entity = PaymentSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["partId"]),
        Index(value = ["sessionId"]),
        Index(value = ["status"]),
        Index(value = ["clientReference"])
    ]
)
data class PaymentPartEntity(
    @PrimaryKey
    val partId: String,
    val sessionId: String,
    val sequenceNumber: Int,
    val totalParts: Int,
    val amountPaise: Long,
    val clientReference: String,
    val upiTransactionReference: String?,
    val generatedUri: String,
    val status: String,
    val createdAt: Long,
    val paidAt: Long?
)
