package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.core.payment.PaymentReferenceGenerator
import com.qrspliter.adil.core.payment.UpiPaymentUriBuilder
import com.qrspliter.adil.core.validation.InputValidator
import com.qrspliter.adil.domain.model.Money
import com.qrspliter.adil.domain.model.PaymentPart
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.model.SplitPolicy
import com.qrspliter.adil.domain.repository.PaymentRepository
import com.qrspliter.adil.domain.repository.SettingsRepository
import java.util.UUID

class CreatePaymentSessionUseCase(
    private val paymentRepository: PaymentRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(
        totalAmountString: String,
        merchantVpa: String,
        merchantName: String,
        customReference: String?,
        note: String?
    ): Result<PaymentSession> {
        val amountVal = InputValidator.validateAmount(totalAmountString)
        if (amountVal is InputValidator.ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(amountVal.message))
        }

        val vpaVal = InputValidator.validateVpa(merchantVpa)
        if (vpaVal is InputValidator.ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(vpaVal.message))
        }

        val nameVal = InputValidator.validateMerchantName(merchantName)
        if (nameVal is InputValidator.ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(nameVal.message))
        }

        val refVal = InputValidator.validateReference(customReference)
        if (refVal is InputValidator.ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(refVal.message))
        }

        val noteVal = InputValidator.validateNote(note)
        if (noteVal is InputValidator.ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(noteVal.message))
        }

        val money = Money.fromRupeesString(totalAmountString.trim().replace(",", ""))
        val sessionRef = if (!customReference.isNullOrBlank()) {
            customReference.trim()
        } else {
            PaymentReferenceGenerator.generateSessionReference()
        }

        val maxChunk = settingsRepository.getMaxChunkAmount()
        val splitPolicy = SplitPolicy(maxChunk)

        val splitChunks = PaymentSplitEngine.calculateSplit(money, splitPolicy)
        val sessionId = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()

        val parts = splitChunks.mapIndexed { index, chunkAmount ->
            val sequence = index + 1
            val partRef = PaymentReferenceGenerator.generatePartReference(sessionRef, sequence)
            val uri = UpiPaymentUriBuilder.buildUri(
                vpa = merchantVpa.trim(),
                name = merchantName.trim(),
                amount = chunkAmount,
                transactionRef = partRef,
                note = note?.trim()
            )

            PaymentPart(
                partId = UUID.randomUUID().toString(),
                sessionId = sessionId,
                sequenceNumber = sequence,
                totalParts = splitChunks.size,
                amount = chunkAmount,
                clientReference = partRef,
                generatedUri = uri,
                createdAt = createdAt
            )
        }

        val session = PaymentSession(
            sessionId = sessionId,
            totalAmount = money,
            merchantVpa = merchantVpa.trim(),
            merchantName = merchantName.trim(),
            referenceId = sessionRef,
            note = note?.trim(),
            createdAt = createdAt,
            parts = parts
        )

        paymentRepository.savePaymentSession(session)
        return Result.success(session)
    }
}
