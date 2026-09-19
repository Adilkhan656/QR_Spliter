package com.qrspliter.adil.domain.model

enum class PaymentPartStatus(val displayName: String) {
    PENDING("Pending"),
    USER_REPORTED_PAID("Paid"),
    VERIFIED("Verified"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    UNKNOWN("Unknown")
}
