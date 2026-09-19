package com.qrspliter.adil.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyTest {

    @Test
    fun `test money creation from rupees and paise`() {
        val m1 = Money.fromRupees(100L)
        assertEquals(10000L, m1.paise)

        val m2 = Money.fromPaise(199900L)
        assertEquals(199900L, m2.paise)
    }

    @Test
    fun `test money parsing from string accurately`() {
        assertEquals(600000L, Money.fromRupeesString("6000").paise)
        assertEquals(600000L, Money.fromRupeesString("6,000").paise)
        assertEquals(199950L, Money.fromRupeesString("1999.50").paise)
        assertEquals(199905L, Money.fromRupeesString("1999.05").paise)
        assertEquals(50L, Money.fromRupeesString("0.50").paise)
        assertEquals(0L, Money.fromRupeesString("0").paise)
    }

    @Test
    fun `test upi amount string formatting`() {
        assertEquals("1999.00", Money.fromRupees(1999L).toUpiAmountString)
        assertEquals("3.00", Money.fromRupees(3L).toUpiAmountString)
        assertEquals("0.50", Money(50L).toUpiAmountString)
        assertEquals("1234.56", Money(123456L).toUpiAmountString)
    }

    @Test
    fun `test money addition and subtraction`() {
        val a = Money.fromRupees(1999L)
        val b = Money.fromRupees(1L)

        val sum = a + b
        assertEquals(200000L, sum.paise)

        val diff = sum - b
        assertEquals(199900L, diff.paise)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test money subtraction resulting in negative throws exception`() {
        val a = Money.fromRupees(10L)
        val b = Money.fromRupees(20L)
        val result = a - b
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test negative paise initialization throws exception`() {
        Money(-100L)
    }
}
