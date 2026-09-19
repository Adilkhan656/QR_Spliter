package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.domain.model.Money
import com.qrspliter.adil.domain.model.SplitPolicy

object PaymentSplitEngine {

    fun calculateSplit(
        totalAmount: Money,
        policy: SplitPolicy = SplitPolicy()
    ): List<Money> {
        require(totalAmount.paise > 0) { "Total amount must be greater than zero" }
        require(policy.maxChunkAmount.paise > 0) { "Max chunk amount must be positive" }

        val totalPaise = totalAmount.paise
        val maxChunkPaise = policy.maxChunkAmount.paise

        if (totalPaise <= maxChunkPaise) {
            return listOf(totalAmount)
        }

        val fullChunksCount = (totalPaise / maxChunkPaise).toInt()
        val remainderPaise = totalPaise % maxChunkPaise

        val parts = ArrayList<Money>(fullChunksCount + if (remainderPaise > 0) 1 else 0)

        repeat(fullChunksCount) {
            parts.add(Money(maxChunkPaise))
        }

        if (remainderPaise > 0) {
            parts.add(Money(remainderPaise))
        }

        val calculatedSum = parts.sumOf { it.paise }
        check(calculatedSum == totalPaise) {
            "Inconsistency in split engine calculation: expected $totalPaise paise but calculated $calculatedSum paise"
        }

        return parts
    }
}
