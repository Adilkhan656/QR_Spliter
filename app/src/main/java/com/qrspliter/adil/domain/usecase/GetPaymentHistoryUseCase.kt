package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentHistoryUseCase(
    private val paymentRepository: PaymentRepository
) {
    fun execute(): Flow<List<PaymentSession>> {
        return paymentRepository.observeAllSessions()
    }
}
