package com.qrspliter.adil.domain.model

enum class MarkAsPaidConfirmationPolicy(val displayName: String) {
    EVERY_TIME("Confirm Every Time"),
    ONCE_PER_SESSION("Confirm Once Per Session"),
    NEVER("Never Ask Confirmation");

    companion object {
        fun fromName(name: String?): MarkAsPaidConfirmationPolicy {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: EVERY_TIME
        }
    }
}
