package com.qrspliter.adil.domain.model

data class PaymentPart(
    val partId: String,
    val sessionId: String,
    val sequenceNumber: Int,
    val totalParts: Int,
    val amount: Money,
    val clientReference: String,
    val upiTransactionReference: String? = null,
    val generatedUri: String,
    val status: PaymentPartStatus = PaymentPartStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null
)
