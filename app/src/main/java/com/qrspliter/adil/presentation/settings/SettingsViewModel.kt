package com.qrspliter.adil.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.MarkAsPaidConfirmationPolicy
import com.qrspliter.adil.domain.model.MerchantInfo
import com.qrspliter.adil.domain.model.Money
import com.qrspliter.adil.domain.repository.PaymentRepository
import com.qrspliter.adil.domain.repository.SettingsRepository
import com.qrspliter.adil.domain.usecase.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SaveSettingsState {
    object Idle : SaveSettingsState()
    object Success : SaveSettingsState()
    data class Error(val message: String) : SaveSettingsState()
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val paymentRepository: PaymentRepository,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveSettingsState>(SaveSettingsState.Idle)
    val saveState: StateFlow<SaveSettingsState> = _saveState.asStateFlow()

    private val _merchantInfo = MutableStateFlow(MerchantInfo("", ""))
    val merchantInfo: StateFlow<MerchantInfo> = _merchantInfo.asStateFlow()

    private val _maxChunkAmount = MutableStateFlow(Money.fromRupees(1999L))
    val maxChunkAmount: StateFlow<Money> = _maxChunkAmount.asStateFlow()

    private val _retentionPolicy = MutableStateFlow(DataRetentionPolicy.NEVER)
    val retentionPolicy: StateFlow<DataRetentionPolicy> = _retentionPolicy.asStateFlow()

    private val _confirmationPolicy = MutableStateFlow(MarkAsPaidConfirmationPolicy.EVERY_TIME)
    val confirmationPolicy: StateFlow<MarkAsPaidConfirmationPolicy> = _confirmationPolicy.asStateFlow()

    private val _customDays = MutableStateFlow(7)
    val customDays: StateFlow<Int> = _customDays.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _merchantInfo.value = settingsRepository.getMerchantInfo()
            _maxChunkAmount.value = settingsRepository.getMaxChunkAmount()
            _retentionPolicy.value = settingsRepository.getDataRetentionPolicy()
            _confirmationPolicy.value = settingsRepository.getConfirmationPolicy()
            _customDays.value = settingsRepository.getCustomRetentionDays()
        }
    }

    fun saveSettings(
        vpa: String,
        name: String,
        maxChunkString: String,
        retentionPolicy: DataRetentionPolicy,
        confirmationPolicy: MarkAsPaidConfirmationPolicy,
        customDaysString: String
    ) {
        _saveState.value = SaveSettingsState.Idle
        viewModelScope.launch {
            val merchantRes = saveSettingsUseCase.saveMerchantInfo(vpa, name)
            if (merchantRes.isFailure) {
                _saveState.value = SaveSettingsState.Error(merchantRes.exceptionOrNull()?.message ?: "Invalid Merchant Info")
                return@launch
            }

            try {
                val chunkMoney = Money.fromRupeesString(maxChunkString)
                val chunkRes = saveSettingsUseCase.saveMaxChunkAmount(chunkMoney)
                if (chunkRes.isFailure) {
                    _saveState.value = SaveSettingsState.Error(chunkRes.exceptionOrNull()?.message ?: "Invalid Chunk Amount")
                    return@launch
                }
            } catch (e: Exception) {
                _saveState.value = SaveSettingsState.Error("Invalid chunk amount format")
                return@launch
            }

            val customDaysVal = customDaysString.toIntOrNull() ?: 7
            settingsRepository.saveDataRetentionPolicy(retentionPolicy)
            settingsRepository.saveConfirmationPolicy(confirmationPolicy)
            settingsRepository.saveCustomRetentionDays(customDaysVal)

            // Trigger immediate cleanup based on updated retention policy
            paymentRepository.cleanupExpiredSessions(retentionPolicy, customDaysVal)

            _saveState.value = SaveSettingsState.Success
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            paymentRepository.clearAllHistory()
            _saveState.value = SaveSettingsState.Success
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val paymentRepository: PaymentRepository,
        private val saveSettingsUseCase: SaveSettingsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, paymentRepository, saveSettingsUseCase) as T
        }
    }
}
