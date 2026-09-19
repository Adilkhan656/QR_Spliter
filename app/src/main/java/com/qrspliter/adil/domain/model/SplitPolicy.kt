package com.qrspliter.adil.domain.model

/**
 * Domain policy configuring how payments are chunked.
 * Note: This is an application splitting preference, NOT a legal or regulatory MDR threshold.
 */
data class SplitPolicy(
    val maxChunkAmount: Money = Money.fromRupees(1999L) // Default ₹1,999 (199900 paise)
) {
    init {
        require(maxChunkAmount.paise > 0) { "Maximum chunk amount must be positive" }
    }
}
