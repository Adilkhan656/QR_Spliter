package com.qrspliter.adil.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.repository.PaymentRepository
import com.qrspliter.adil.domain.usecase.GetPaymentSessionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PaymentDetailsViewModel(
    private val sessionId: String,
    getPaymentSessionUseCase: GetPaymentSessionUseCase,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    val sessionFlow: Flow<PaymentSession?> = getPaymentSessionUseCase.observeSession(sessionId)

    fun deleteSession() {
        viewModelScope.launch {
            paymentRepository.deletePaymentSession(sessionId)
        }
    }

    class Factory(
        private val sessionId: String,
        private val getPaymentSessionUseCase: GetPaymentSessionUseCase,
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentDetailsViewModel(sessionId, getPaymentSessionUseCase, paymentRepository) as T
        }
    }
}
