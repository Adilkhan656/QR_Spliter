package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.domain.model.Money
import com.qrspliter.adil.domain.model.SplitPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class PaymentSplitEngineTest {

    @Test
    fun `test split for 1 rupee`() {
        val total = Money.fromRupees(1L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(1, result.size)
        assertEquals(Money.fromRupees(1L), result[0])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test split for 1999 rupees`() {
        val total = Money.fromRupees(1999L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(1, result.size)
        assertEquals(Money.fromRupees(1999L), result[0])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test split for 2000 rupees`() {
        val total = Money.fromRupees(2000L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(2, result.size)
        assertEquals(Money.fromRupees(1999L), result[0])
        assertEquals(Money.fromRupees(1L), result[1])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test split for 2001 rupees`() {
        val total = Money.fromRupees(2001L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(2, result.size)
        assertEquals(Money.fromRupees(1999L), result[0])
        assertEquals(Money.fromRupees(2L), result[1])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test split for 3000 rupees`() {
        val total = Money.fromRupees(3000L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(2, result.size)
        assertEquals(Money.fromRupees(1999L), result[0])
        assertEquals(Money.fromRupees(1001L), result[1])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test split for 5000 rupees`() {
        val total = Money.fromRupees(5000L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(3, result.size)
        assertEquals(Money.fromRupees(1999L), result[0])
        assertEquals(Money.fromRupees(1999L), result[1])
        assertEquals(Money.fromRupees(1002L), result[2])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test split for 6000 rupees`() {
        val total = Money.fromRupees(6000L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(4, result.size)
        assertEquals(Money.fromRupees(1999L), result[0])
        assertEquals(Money.fromRupees(1999L), result[1])
        assertEquals(Money.fromRupees(1999L), result[2])
        assertEquals(Money.fromRupees(3L), result[3])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test split for 10000 rupees`() {
        val total = Money.fromRupees(10000L)
        val result = PaymentSplitEngine.calculateSplit(total)

        assertEquals(6, result.size)
        for (i in 0..4) {
            assertEquals(Money.fromRupees(1999L), result[i])
        }
        assertEquals(Money.fromRupees(5L), result[5])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test
    fun `test custom split policy max chunk 500 rupees`() {
        val total = Money.fromRupees(1200L)
        val customPolicy = SplitPolicy(maxChunkAmount = Money.fromRupees(500L))
        val result = PaymentSplitEngine.calculateSplit(total, customPolicy)

        assertEquals(3, result.size)
        assertEquals(Money.fromRupees(500L), result[0])
        assertEquals(Money.fromRupees(500L), result[1])
        assertEquals(Money.fromRupees(200L), result[2])
        assertEquals(total.paise, result.sumOf { it.paise })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test zero amount throws exception`() {
        PaymentSplitEngine.calculateSplit(Money.ZERO)
    }
}
