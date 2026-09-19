package com.qrspliter.adil.core.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidatorTest {

    @Test
    fun `test validate amount`() {
        assertTrue(InputValidator.validateAmount("6000").isValid)
        assertTrue(InputValidator.validateAmount("1999.50").isValid)
        assertTrue(InputValidator.validateAmount("1").isValid)

        assertFalse(InputValidator.validateAmount("0").isValid)
        assertFalse(InputValidator.validateAmount("-500").isValid)
        assertFalse(InputValidator.validateAmount("abc").isValid)
        assertFalse(InputValidator.validateAmount("").isValid)
    }

    @Test
    fun `test validate vpa`() {
        assertTrue(InputValidator.validateVpa("merchant@upi").isValid)
        assertTrue(InputValidator.validateVpa("user.name123@okicici").isValid)
        assertTrue(InputValidator.validateVpa("store_01@ybl").isValid)

        assertFalse(InputValidator.validateVpa("invalidvpa").isValid)
        assertFalse(InputValidator.validateVpa("user@").isValid)
        assertFalse(InputValidator.validateVpa("@upi").isValid)
        assertFalse(InputValidator.validateVpa("user@upi@bad").isValid)
        assertFalse(InputValidator.validateVpa("user\n@upi").isValid)
        assertFalse(InputValidator.validateVpa("").isValid)
    }

    @Test
    fun `test validate merchant name`() {
        assertTrue(InputValidator.validateMerchantName("ABC Store").isValid)
        assertTrue(InputValidator.validateMerchantName("Store #123").isValid)

        assertFalse(InputValidator.validateMerchantName("").isValid)
        assertFalse(InputValidator.validateMerchantName("Store\nName").isValid)
    }

    @Test
    fun `test validate reference and note`() {
        assertTrue(InputValidator.validateReference("INV-2026-001").isValid)
        assertTrue(InputValidator.validateReference(null).isValid)
        assertTrue(InputValidator.validateNote("Purchase note").isValid)

        assertFalse(InputValidator.validateReference("INV\n2026").isValid)
        assertFalse(InputValidator.validateNote("Note\r\nBad").isValid)
    }
}
