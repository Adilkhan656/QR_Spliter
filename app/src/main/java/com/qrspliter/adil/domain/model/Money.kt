package com.qrspliter.adil.domain.model

import java.text.NumberFormat
import java.util.Locale

/**
 * Value object representing monetary value strictly in paise (Long) to avoid floating point errors.
 * 1 INR = 100 paise.
 */
data class Money(val paise: Long) : Comparable<Money> {

    init {
        require(paise >= 0) { "Monetary value cannot be negative: $paise" }
    }

    val inRupeesDouble: Double
        get() = paise / 100.0

    val inRupeesLong: Long
        get() = paise / 100

    val formattedRupees: String
        get() {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            return format.format(inRupeesDouble)
        }

    val toUpiAmountString: String
        get() {
            val rupees = paise / 100
            val remainingPaise = paise % 100
            return String.format(Locale.US, "%d.%02d", rupees, remainingPaise)
        }

    operator fun plus(other: Money): Money = Money(this.paise + other.paise)

    operator fun minus(other: Money): Money {
        val result = this.paise - other.paise
        require(result >= 0) { "Money subtraction resulted in negative value" }
        return Money(result)
    }

    override fun compareTo(other: Money): Int = this.paise.compareTo(other.paise)

    companion object {
        val ZERO = Money(0L)

        fun fromPaise(paise: Long): Money = Money(paise)

        fun fromRupees(rupees: Long): Money = Money(rupees * 100L)

        /**
         * Parses a string representation of money (e.g. "6000", "1999.50", "0.5") into Money without
         * using floating-point arithmetic to prevent rounding errors.
         */
        fun fromRupeesString(input: String): Money {
            val clean = input.trim().replace(",", "")
            if (clean.isEmpty()) return ZERO

            val parts = clean.split(".")
            if (parts.size > 2) throw IllegalArgumentException("Invalid amount format: $input")

            val rupees = parts[0].ifEmpty { "0" }.toLong()
            val paise = if (parts.size == 2) {
                val pString = parts[1]
                when {
                    pString.isEmpty() -> 0L
                    pString.length == 1 -> pString.toLong() * 10L
                    pString.length == 2 -> pString.toLong()
                    else -> pString.substring(0, 2).toLong() // truncate excess precision
                }
            } else {
                0L
            }

            return Money(rupees * 100L + paise)
        }
    }
}
