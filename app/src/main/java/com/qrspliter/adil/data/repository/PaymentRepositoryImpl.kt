package com.qrspliter.adil.data.repository

import com.qrspliter.adil.data.local.dao.PaymentPartDao
import com.qrspliter.adil.data.local.dao.PaymentSessionDao
import com.qrspliter.adil.data.mapper.PaymentMapper
import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.model.PaymentSessionStatus
import com.qrspliter.adil.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PaymentRepositoryImpl(
    private val sessionDao: PaymentSessionDao,
    private val partDao: PaymentPartDao
) : PaymentRepository {

    override suspend fun savePaymentSession(session: PaymentSession) {
        val sessionEntity = PaymentMapper.toEntitySession(session)
        val partEntities = session.parts.map { PaymentMapper.toEntityPart(it) }

        sessionDao.insertSession(sessionEntity)
        partDao.insertParts(partEntities)
    }

    override suspend fun getPaymentSessionById(sessionId: String): PaymentSession? {
        val sessionEntity = sessionDao.getSessionById(sessionId) ?: return null
        val partEntities = partDao.getPartsForSession(sessionId)
        return PaymentMapper.toDomainSession(sessionEntity, partEntities)
    }

    override fun observeAllSessions(): Flow<List<PaymentSession>> {
        return sessionDao.observeAllSessions().map { sessionEntities ->
            sessionEntities.map { sessionEntity ->
                val parts = partDao.getPartsForSession(sessionEntity.sessionId)
                PaymentMapper.toDomainSession(sessionEntity, parts)
            }
        }
    }

    override fun observeSessionById(sessionId: String): Flow<PaymentSession?> {
        val sessionFlow = sessionDao.observeSessionById(sessionId)
        val partsFlow = partDao.observePartsForSession(sessionId)

        return combine(sessionFlow, partsFlow) { sessionEntity, partEntities ->
            if (sessionEntity == null) null
            else PaymentMapper.toDomainSession(sessionEntity, partEntities)
        }
    }

    override suspend fun updatePartStatus(partId: String, status: PaymentPartStatus, paidAt: Long?) {
        val partEntity = partDao.getPartById(partId) ?: return
        partDao.updatePartStatus(partId, status.name, paidAt)

        // Re-evaluate parent session status
        val allParts = partDao.getPartsForSession(partEntity.sessionId)
        val paidCount = allParts.count {
            it.status == PaymentPartStatus.USER_REPORTED_PAID.name ||
                    it.status == PaymentPartStatus.VERIFIED.name
        }
        val totalCount = allParts.size

        val newSessionStatus = when {
            paidCount == totalCount -> PaymentSessionStatus.PAID
            paidCount > 0 -> PaymentSessionStatus.PARTIALLY_PAID
            allParts.all { it.status == PaymentPartStatus.CANCELLED.name } -> PaymentSessionStatus.CANCELLED
            else -> PaymentSessionStatus.PENDING
        }

        sessionDao.updateSessionStatus(partEntity.sessionId, newSessionStatus.name)
    }

    override suspend fun deletePaymentSession(sessionId: String) {
        sessionDao.deleteSession(sessionId)
    }

    override suspend fun cleanupExpiredSessions(policy: DataRetentionPolicy, customDays: Int): Int {
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L

        return when (policy) {
            DataRetentionPolicy.NEVER -> 0
            DataRetentionPolicy.IMMEDIATELY_AFTER_VIEWING -> sessionDao.deleteAllSessions()
            DataRetentionPolicy.ONE_DAY -> {
                val cutoff = now - (1 * dayInMillis)
                sessionDao.deleteSessionsOlderThan(cutoff)
            }
            DataRetentionPolicy.THIRTY_DAYS -> {
                val cutoff = now - (30 * dayInMillis)
                sessionDao.deleteSessionsOlderThan(cutoff)
            }
            DataRetentionPolicy.CUSTOM_DAYS -> {
                val days = customDays.coerceAtLeast(1)
                val cutoff = now - (days * dayInMillis)
                sessionDao.deleteSessionsOlderThan(cutoff)
            }
        }
    }

    override suspend fun clearAllHistory(): Int {
        return sessionDao.deleteAllSessions()
    }
}
