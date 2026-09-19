package com.qrspliter.adil.domain.model

enum class PaymentSessionStatus(val displayName: String) {
    PENDING("Pending"),
    PARTIALLY_PAID("Partially Paid"),
    PAID("Paid"),
    CANCELLED("Cancelled")
}
