package com.qrspliter.adil.presentation.createpayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qrspliter.adil.domain.model.MerchantInfo
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.repository.SettingsRepository
import com.qrspliter.adil.domain.usecase.CreatePaymentSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CreatePaymentState {
    object Idle : CreatePaymentState()
    object Loading : CreatePaymentState()
    data class Success(val session: PaymentSession) : CreatePaymentState()
    data class Error(val message: String) : CreatePaymentState()
}

class CreatePaymentViewModel(
    private val createPaymentSessionUseCase: CreatePaymentSessionUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreatePaymentState>(CreatePaymentState.Idle)
    val uiState: StateFlow<CreatePaymentState> = _uiState.asStateFlow()

    private val _defaultMerchantInfo = MutableStateFlow(MerchantInfo("", ""))
    val defaultMerchantInfo: StateFlow<MerchantInfo> = _defaultMerchantInfo.asStateFlow()

    init {
        loadDefaultMerchantInfo()
    }

    private fun loadDefaultMerchantInfo() {
        viewModelScope.launch {
            val merchantInfo = settingsRepository.getMerchantInfo()
            _defaultMerchantInfo.value = merchantInfo
        }
    }

    fun createPaymentSession(
        amount: String,
        vpa: String,
        name: String,
        reference: String?,
        note: String?
    ) {
        _uiState.value = CreatePaymentState.Loading
        viewModelScope.launch {
            val result = createPaymentSessionUseCase(
                totalAmountString = amount,
                merchantVpa = vpa,
                merchantName = name,
                customReference = reference,
                note = note
            )
            result.fold(
                onSuccess = { session ->
                    _uiState.value = CreatePaymentState.Success(session)
                },
                onFailure = { error ->
                    _uiState.value = CreatePaymentState.Error(error.message ?: "Failed to create payment session")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = CreatePaymentState.Idle
    }

    class Factory(
        private val createPaymentSessionUseCase: CreatePaymentSessionUseCase,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreatePaymentViewModel(createPaymentSessionUseCase, settingsRepository) as T
        }
    }
}
