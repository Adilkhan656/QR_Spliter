package com.qrspliter.adil.data.mapper

import com.qrspliter.adil.data.local.entity.PaymentPartEntity
import com.qrspliter.adil.data.local.entity.PaymentSessionEntity
import com.qrspliter.adil.domain.model.Money
import com.qrspliter.adil.domain.model.PaymentPart
import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.model.PaymentSessionStatus

object PaymentMapper {

    fun toDomainSession(
        entity: PaymentSessionEntity,
        partEntities: List<PaymentPartEntity>
    ): PaymentSession {
        val parts = partEntities.map { toDomainPart(it) }
        val sessionStatus = try {
            PaymentSessionStatus.valueOf(entity.status)
        } catch (_: Exception) {
            PaymentSessionStatus.PENDING
        }

        return PaymentSession(
            sessionId = entity.sessionId,
            totalAmount = Money(entity.totalAmountPaise),
            merchantVpa = entity.merchantVpa,
            merchantName = entity.merchantName,
            referenceId = entity.referenceId,
            note = entity.note,
            createdAt = entity.createdAt,
            status = sessionStatus,
            parts = parts,
            isRead = entity.isRead
        )
    }

    fun toEntitySession(domain: PaymentSession): PaymentSessionEntity {
        return PaymentSessionEntity(
            sessionId = domain.sessionId,
            totalAmountPaise = domain.totalAmount.paise,
            merchantVpa = domain.merchantVpa,
            merchantName = domain.merchantName,
            referenceId = domain.referenceId,
            note = domain.note,
            status = domain.status.name,
            createdAt = domain.createdAt,
            isRead = domain.isRead
        )
    }

    fun toDomainPart(entity: PaymentPartEntity): PaymentPart {
        val partStatus = try {
            PaymentPartStatus.valueOf(entity.status)
        } catch (_: Exception) {
            PaymentPartStatus.PENDING
        }

        return PaymentPart(
            partId = entity.partId,
            sessionId = entity.sessionId,
            sequenceNumber = entity.sequenceNumber,
            totalParts = entity.totalParts,
            amount = Money(entity.amountPaise),
            clientReference = entity.clientReference,
            upiTransactionReference = entity.upiTransactionReference,
            generatedUri = entity.generatedUri,
            status = partStatus,
            createdAt = entity.createdAt,
            paidAt = entity.paidAt
        )
    }

    fun toEntityPart(domain: PaymentPart): PaymentPartEntity {
        return PaymentPartEntity(
            partId = domain.partId,
            sessionId = domain.sessionId,
            sequenceNumber = domain.sequenceNumber,
            totalParts = domain.totalParts,
            amountPaise = domain.amount.paise,
            clientReference = domain.clientReference,
            upiTransactionReference = domain.upiTransactionReference,
            generatedUri = domain.generatedUri,
            status = domain.status.name,
            createdAt = domain.createdAt,
            paidAt = domain.paidAt
        )
    }
}
