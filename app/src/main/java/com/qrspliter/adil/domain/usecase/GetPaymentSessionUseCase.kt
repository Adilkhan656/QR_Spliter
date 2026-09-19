package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentSessionUseCase(
    private val paymentRepository: PaymentRepository
) {
    fun observeSession(sessionId: String): Flow<PaymentSession?> {
        return paymentRepository.observeSessionById(sessionId)
    }

    suspend fun getSession(sessionId: String): PaymentSession? {
        return paymentRepository.getPaymentSessionById(sessionId)
    }
}
