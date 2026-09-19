package com.qrspliter.adil.data.repository

import com.qrspliter.adil.core.security.SecurityPreferences
import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.MarkAsPaidConfirmationPolicy
import com.qrspliter.adil.domain.model.MerchantInfo
import com.qrspliter.adil.domain.model.Money
import com.qrspliter.adil.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val securityPreferences: SecurityPreferences
) : SettingsRepository {

    private val merchantInfoFlow = MutableStateFlow(fetchCurrentMerchantInfo())
    private val maxChunkFlow = MutableStateFlow(securityPreferences.getMaxChunkAmount())
    private val retentionPolicyFlow = MutableStateFlow(securityPreferences.getDataRetentionPolicy())
    private val customRetentionDaysFlow = MutableStateFlow(securityPreferences.getCustomRetentionDays())
    private val confirmationPolicyFlow = MutableStateFlow(securityPreferences.getConfirmationPolicy())

    private fun fetchCurrentMerchantInfo(): MerchantInfo {
        return MerchantInfo(
            vpa = securityPreferences.getDefaultVpa(),
            name = securityPreferences.getDefaultMerchantName()
        )
    }

    override fun observeMerchantInfo(): Flow<MerchantInfo> = merchantInfoFlow.asStateFlow()

    override suspend fun getMerchantInfo(): MerchantInfo = fetchCurrentMerchantInfo()

    override suspend fun saveMerchantInfo(vpa: String, name: String) {
        securityPreferences.setDefaultVpa(vpa)
        securityPreferences.setDefaultMerchantName(name)
        merchantInfoFlow.value = fetchCurrentMerchantInfo()
    }

    override fun observeMaxChunkAmount(): Flow<Money> = maxChunkFlow.asStateFlow()

    override suspend fun getMaxChunkAmount(): Money = securityPreferences.getMaxChunkAmount()

    override suspend fun saveMaxChunkAmount(amount: Money) {
        securityPreferences.setMaxChunkAmount(amount)
        maxChunkFlow.value = amount
    }

    override fun observeDataRetentionPolicy(): Flow<DataRetentionPolicy> = retentionPolicyFlow.asStateFlow()

    override suspend fun getDataRetentionPolicy(): DataRetentionPolicy = securityPreferences.getDataRetentionPolicy()

    override suspend fun saveDataRetentionPolicy(policy: DataRetentionPolicy) {
        securityPreferences.setDataRetentionPolicy(policy)
        retentionPolicyFlow.value = policy
    }

    override fun observeCustomRetentionDays(): Flow<Int> = customRetentionDaysFlow.asStateFlow()

    override suspend fun getCustomRetentionDays(): Int = securityPreferences.getCustomRetentionDays()

    override suspend fun saveCustomRetentionDays(days: Int) {
        securityPreferences.setCustomRetentionDays(days)
        customRetentionDaysFlow.value = days
    }

    override fun observeConfirmationPolicy(): Flow<MarkAsPaidConfirmationPolicy> = confirmationPolicyFlow.asStateFlow()

    override suspend fun getConfirmationPolicy(): MarkAsPaidConfirmationPolicy = securityPreferences.getConfirmationPolicy()

    override suspend fun saveConfirmationPolicy(policy: MarkAsPaidConfirmationPolicy) {
        securityPreferences.setConfirmationPolicy(policy)
        confirmationPolicyFlow.value = policy
    }
}
