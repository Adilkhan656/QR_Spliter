package com.qrspliter.adil.core.validation

import com.qrspliter.adil.domain.model.Money

object InputValidator {

    private val VPA_REGEX = Regex("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z][a-zA-Z0-9]{2,64}$")
    private const val MAX_NAME_LENGTH = 100
    private const val MAX_REF_LENGTH = 50
    private const val MAX_NOTE_LENGTH = 100

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()

        val isValid: Boolean get() = this is Success
    }

    fun validateAmount(amountString: String): ValidationResult {
        if (amountString.isBlank()) {
            return ValidationResult.Error("Amount cannot be empty")
        }
        val clean = amountString.trim().replace(",", "")
        return try {
            val money = Money.fromRupeesString(clean)
            when {
                money.paise <= 0L -> ValidationResult.Error("Amount must be greater than zero")
                money.paise > 1_000_000_00L -> ValidationResult.Error("Amount exceeds maximum limit (₹10,00,000)")
                else -> ValidationResult.Success
            }
        } catch (_: Exception) {
            ValidationResult.Error("Invalid amount format")
        }
    }

    fun validateVpa(vpa: String): ValidationResult {
        val trimmed = vpa.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("UPI ID / VPA cannot be empty")
        }
        if (hasControlCharacters(trimmed)) {
            return ValidationResult.Error("UPI ID contains invalid characters")
        }
        if (!VPA_REGEX.matches(trimmed)) {
            return ValidationResult.Error("Invalid UPI ID format (e.g. merchant@upi)")
        }
        return ValidationResult.Success
    }

    fun validateMerchantName(name: String): ValidationResult {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Merchant name cannot be empty")
        }
        if (trimmed.length > MAX_NAME_LENGTH) {
            return ValidationResult.Error("Merchant name is too long (max $MAX_NAME_LENGTH characters)")
        }
        if (hasControlCharacters(trimmed)) {
            return ValidationResult.Error("Merchant name contains invalid characters")
        }
        return ValidationResult.Success
    }

    fun validateReference(reference: String?): ValidationResult {
        if (reference.isNullOrBlank()) return ValidationResult.Success
        val trimmed = reference?.trim() ?: ""
        if (trimmed.length > MAX_REF_LENGTH) {
            return ValidationResult.Error("Reference ID is too long (max $MAX_REF_LENGTH characters)")
        }
        if (hasControlCharacters(trimmed)) {
            return ValidationResult.Error("Reference ID contains invalid characters")
        }
        return ValidationResult.Success
    }

    fun validateNote(note: String?): ValidationResult {
        if (note.isNullOrBlank()) return ValidationResult.Success
        val trimmed = note?.trim() ?: ""
        if (trimmed.length > MAX_NOTE_LENGTH) {
            return ValidationResult.Error("Note is too long (max $MAX_NOTE_LENGTH characters)")
        }
        if (hasControlCharacters(trimmed)) {
            return ValidationResult.Error("Note contains invalid characters")
        }
        return ValidationResult.Success
    }

    private fun hasControlCharacters(text: String): Boolean {
        return text.any { it.isISOControl() || it == '\n' || it == '\r' || it == '\t' }
    }
}
