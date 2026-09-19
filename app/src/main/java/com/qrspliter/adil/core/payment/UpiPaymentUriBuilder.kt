package com.qrspliter.adil.core.payment

import com.qrspliter.adil.domain.model.Money
import java.net.URI
import java.net.URLEncoder

object UpiPaymentUriBuilder {

    private const val SCHEME = "upi"
    private const val AUTHORITY = "pay"
    private const val CURRENCY_INR = "INR"

    fun buildUri(
        vpa: String,
        name: String,
        amount: Money,
        transactionRef: String,
        note: String? = null
    ): String {
        require(vpa.isNotBlank()) { "VPA cannot be blank" }
        require(name.isNotBlank()) { "Merchant name cannot be blank" }
        require(amount.paise > 0) { "Amount must be greater than zero" }
        require(transactionRef.isNotBlank()) { "Transaction reference cannot be blank" }

        val params = ArrayList<Pair<String, String>>()
        params.add("pa" to vpa.trim())
        params.add("pn" to name.trim())
        params.add("am" to amount.toUpiAmountString)
        params.add("cu" to CURRENCY_INR)
        params.add("tr" to transactionRef.trim())

        if (!note.isNullOrBlank()) {
            params.add("tn" to note.trim())
        }

        val queryString = params.joinToString("&") { (key, value) ->
            "${encode(key)}=${encode(value)}"
        }

        val uriString = "$SCHEME://$AUTHORITY?$queryString"

        check(isValidUpiUri(uriString)) { "Generated UPI URI failed validation check" }

        return uriString
    }

    fun isValidUpiUri(uriString: String): Boolean {
        return try {
            val uri = URI(uriString)
            if (uri.scheme != SCHEME || uri.host != AUTHORITY) return false

            val query = uri.rawQuery ?: return false
            val paramMap = query.split("&").associate {
                val parts = it.split("=")
                if (parts.size == 2) parts[0] to parts[1] else parts[0] to ""
            }

            paramMap.containsKey("pa") &&
                    paramMap.containsKey("pn") &&
                    paramMap.containsKey("am") &&
                    paramMap["cu"] == CURRENCY_INR
        } catch (_: Exception) {
            false
        }
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, "UTF-8").replace("+", "%20")
    }
}
