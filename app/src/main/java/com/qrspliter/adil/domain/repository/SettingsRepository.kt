package com.qrspliter.adil.domain.repository

import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.MarkAsPaidConfirmationPolicy
import com.qrspliter.adil.domain.model.MerchantInfo
import com.qrspliter.adil.domain.model.Money
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeMerchantInfo(): Flow<MerchantInfo>
    suspend fun getMerchantInfo(): MerchantInfo
    suspend fun saveMerchantInfo(vpa: String, name: String)
    fun observeMaxChunkAmount(): Flow<Money>
    suspend fun getMaxChunkAmount(): Money
    suspend fun saveMaxChunkAmount(amount: Money)
    fun observeDataRetentionPolicy(): Flow<DataRetentionPolicy>
    suspend fun getDataRetentionPolicy(): DataRetentionPolicy
    suspend fun saveDataRetentionPolicy(policy: DataRetentionPolicy)
    fun observeCustomRetentionDays(): Flow<Int>
    suspend fun getCustomRetentionDays(): Int
    suspend fun saveCustomRetentionDays(days: Int)
    fun observeConfirmationPolicy(): Flow<MarkAsPaidConfirmationPolicy>
    suspend fun getConfirmationPolicy(): MarkAsPaidConfirmationPolicy
    suspend fun saveConfirmationPolicy(policy: MarkAsPaidConfirmationPolicy)
}
