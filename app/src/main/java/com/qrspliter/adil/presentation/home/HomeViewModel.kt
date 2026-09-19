package com.qrspliter.adil.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qrspliter.adil.domain.usecase.GetPaymentHistoryUseCase

class HomeViewModel(
    private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase
) : ViewModel() {

    val history = getPaymentHistoryUseCase.execute()

    class Factory(
        private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(getPaymentHistoryUseCase) as T
        }
    }
}
