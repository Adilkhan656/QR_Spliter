package com.qrspliter.adil.core.payment

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object PaymentReferenceGenerator {

    fun generateSessionReference(): String {
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val randomSuffix = UUID.randomUUID().toString().take(8).uppercase(Locale.US)
        return "UPIS-$dateStr-$randomSuffix"
    }

    fun generatePartReference(sessionRef: String, partSequence: Int): String {
        return "$sessionRef-P$partSequence"
    }
}
