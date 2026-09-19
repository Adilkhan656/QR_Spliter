package com.qrspliter.adil.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.repository.PaymentRepository
import com.qrspliter.adil.domain.repository.SettingsRepository
import com.qrspliter.adil.domain.usecase.GetPaymentHistoryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(
    getPaymentHistoryUseCase: GetPaymentHistoryUseCase,
    private val paymentRepository: PaymentRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val historyFlow: Flow<List<PaymentSession>> = getPaymentHistoryUseCase.execute()

    init {
        performRetentionCleanup()
    }

    private fun performRetentionCleanup() {
        viewModelScope.launch {
            val policy = settingsRepository.getDataRetentionPolicy()
            if (policy != DataRetentionPolicy.IMMEDIATELY_AFTER_VIEWING) {
                val customDays = settingsRepository.getCustomRetentionDays()
                paymentRepository.cleanupExpiredSessions(policy, customDays)
            }
        }
    }

    fun onHistoryViewClosed() {
        viewModelScope.launch {
            val policy = settingsRepository.getDataRetentionPolicy()
            if (policy == DataRetentionPolicy.IMMEDIATELY_AFTER_VIEWING) {
                paymentRepository.clearAllHistory()
            }
        }
    }

    class Factory(
        private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase,
        private val paymentRepository: PaymentRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(getPaymentHistoryUseCase, paymentRepository, settingsRepository) as T
        }
    }
}
