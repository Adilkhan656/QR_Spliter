package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.core.validation.InputValidator
import com.qrspliter.adil.domain.model.Money
import com.qrspliter.adil.domain.repository.SettingsRepository

class SaveSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend fun saveMerchantInfo(vpa: String, name: String): Result<Unit> {
        if (vpa.isNotBlank()) {
            val vpaVal = InputValidator.validateVpa(vpa)
            if (vpaVal is InputValidator.ValidationResult.Error) {
                return Result.failure(IllegalArgumentException(vpaVal.message))
            }
        }
        if (name.isNotBlank()) {
            val nameVal = InputValidator.validateMerchantName(name)
            if (nameVal is InputValidator.ValidationResult.Error) {
                return Result.failure(IllegalArgumentException(nameVal.message))
            }
        }
        settingsRepository.saveMerchantInfo(vpa, name)
        return Result.success(Unit)
    }

    suspend fun saveMaxChunkAmount(amount: Money): Result<Unit> {
        if (amount.paise <= 0) {
            return Result.failure(IllegalArgumentException("Max chunk amount must be positive"))
        }
        settingsRepository.saveMaxChunkAmount(amount)
        return Result.success(Unit)
    }
}
