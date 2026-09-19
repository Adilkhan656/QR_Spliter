package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.domain.repository.PaymentRepository

class UpdatePaymentPartStatusUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        partId: String,
        status: PaymentPartStatus,
        paidAt: Long? = System.currentTimeMillis()
    ) {
        paymentRepository.updatePartStatus(partId, status, paidAt)
    }
}
