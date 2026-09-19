package com.qrspliter.adil.domain.repository

import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.domain.model.PaymentSession
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun savePaymentSession(session: PaymentSession)
    suspend fun getPaymentSessionById(sessionId: String): PaymentSession?
    fun observeAllSessions(): Flow<List<PaymentSession>>
    fun observeSessionById(sessionId: String): Flow<PaymentSession?>
    suspend fun updatePartStatus(
        partId: String,
        status: PaymentPartStatus,
        paidAt: Long? = System.currentTimeMillis()
    )
    suspend fun deletePaymentSession(sessionId: String)
    suspend fun deleteSessions(sessionIds: List<String>)
    suspend fun markAllSessionsRead()
    suspend fun cleanupExpiredSessions(policy: DataRetentionPolicy, customDays: Int = 1): Int
    suspend fun clearAllHistory(): Int
}
