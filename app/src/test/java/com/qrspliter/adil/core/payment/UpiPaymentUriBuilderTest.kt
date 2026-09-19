package com.qrspliter.adil.core.payment

import com.qrspliter.adil.domain.model.Money
import org.junit.Assert.assertTrue
import org.junit.Test

class UpiPaymentUriBuilderTest {

    @Test
    fun `test upi uri construction with standard values`() {
        val vpa = "merchant@upi"
        val name = "ABC Store"
        val amount = Money.fromRupees(1999L)
        val ref = "UPIS-20260919-7F92A31C-P1"
        val note = "Invoice 123"

        val uri = UpiPaymentUriBuilder.buildUri(
            vpa = vpa,
            name = name,
            amount = amount,
            transactionRef = ref,
            note = note
        )

        assertTrue(uri.startsWith("upi://pay?"))
        assertTrue(uri.contains("pa=merchant%40upi"))
        assertTrue(uri.contains("pn=ABC%20Store"))
        assertTrue(uri.contains("am=1999.00"))
        assertTrue(uri.contains("cu=INR"))
        assertTrue(uri.contains("tr=UPIS-20260919-7F92A31C-P1"))
        assertTrue(uri.contains("tn=Invoice%20123"))
    }

    @Test
    fun `test upi uri validation helper`() {
        val validUri = "upi://pay?pa=merchant%40upi&pn=Store&am=1999.00&cu=INR&tr=REF123"
        assertTrue(UpiPaymentUriBuilder.isValidUpiUri(validUri))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test blank vpa throws exception`() {
        UpiPaymentUriBuilder.buildUri("", "Store", Money.fromRupees(100L), "REF")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test zero amount throws exception`() {
        UpiPaymentUriBuilder.buildUri("merchant@upi", "Store", Money.ZERO, "REF")
    }
}
