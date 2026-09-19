package com.qrspliter.adil.domain.model

enum class DataRetentionPolicy(val displayName: String) {
    NEVER("Never (Keep History)"),
    IMMEDIATELY_AFTER_VIEWING("Immediately After Viewing"),
    ONE_DAY("Auto-Clear After 1 Day"),
    THIRTY_DAYS("Auto-Clear After 30 Days"),
    CUSTOM_DAYS("Custom Days");

    companion object {
        fun fromName(name: String?): DataRetentionPolicy {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: NEVER
        }
    }
}
