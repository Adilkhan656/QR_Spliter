package com.qrspliter.adil.domain.model

data class PaymentSession(
    val sessionId: String,
    val totalAmount: Money,
    val merchantVpa: String,
    val merchantName: String,
    val referenceId: String,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val status: PaymentSessionStatus = PaymentSessionStatus.PENDING,
    val parts: List<PaymentPart> = emptyList()
) {
    val paidAmount: Money
        get() = Money(parts.filter {
            it.status == PaymentPartStatus.USER_REPORTED_PAID || it.status == PaymentPartStatus.VERIFIED
        }.sumOf { it.amount.paise })

    val remainingAmount: Money
        get() = Money((totalAmount.paise - paidAmount.paise).coerceAtLeast(0L))

    val paidPartsCount: Int
        get() = parts.count {
            it.status == PaymentPartStatus.USER_REPORTED_PAID || it.status == PaymentPartStatus.VERIFIED
        }

    val totalPartsCount: Int
        get() = parts.size
}
