package com.qrspliter.adil.presentation.split

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.usecase.GetPaymentSessionUseCase
import kotlinx.coroutines.flow.Flow

class ReviewSplitViewModel(
    sessionId: String,
    getPaymentSessionUseCase: GetPaymentSessionUseCase
) : ViewModel() {

    val sessionFlow: Flow<PaymentSession?> = getPaymentSessionUseCase.observeSession(sessionId)

    class Factory(
        private val sessionId: String,
        private val getPaymentSessionUseCase: GetPaymentSessionUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReviewSplitViewModel(sessionId, getPaymentSessionUseCase) as T
        }
    }
}
