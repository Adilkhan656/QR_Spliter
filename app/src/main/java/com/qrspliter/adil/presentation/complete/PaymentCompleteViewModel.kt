package com.qrspliter.adil.presentation.complete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.usecase.GetPaymentSessionUseCase
import com.qrspliter.adil.domain.usecase.UpdatePaymentPartStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PaymentCompleteViewModel(
    private val sessionId: String,
    getPaymentSessionUseCase: GetPaymentSessionUseCase,
    private val updatePaymentPartStatusUseCase: UpdatePaymentPartStatusUseCase
) : ViewModel() {

    val sessionFlow: Flow<PaymentSession?> = getPaymentSessionUseCase.observeSession(sessionId)

    fun cancelPaymentSession() {
        viewModelScope.launch {
            val session = sessionFlow.firstOrNull() ?: return@launch
            for (part in session.parts) {
                updatePaymentPartStatusUseCase(
                    partId = part.partId,
                    status = PaymentPartStatus.CANCELLED,
                    paidAt = null
                )
            }
        }
    }

    class Factory(
        private val sessionId: String,
        private val getPaymentSessionUseCase: GetPaymentSessionUseCase,
        private val updatePaymentPartStatusUseCase: UpdatePaymentPartStatusUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentCompleteViewModel(sessionId, getPaymentSessionUseCase, updatePaymentPartStatusUseCase) as T
        }
    }
}
