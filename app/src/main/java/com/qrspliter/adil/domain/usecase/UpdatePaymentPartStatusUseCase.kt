package com.qrspliter.adil.domain.usecase

import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.domain.repository.NotificationRepository
import com.qrspliter.adil.domain.repository.PaymentRepository

class UpdatePaymentPartStatusUseCase(
    private val paymentRepository: PaymentRepository,
    private val notificationRepository: NotificationRepository? = null
) {
    suspend operator fun invoke(
        partId: String,
        status: PaymentPartStatus,
        paidAt: Long? = System.currentTimeMillis()
    ) {
        paymentRepository.updatePartStatus(partId, status, paidAt)

        if (status == PaymentPartStatus.USER_REPORTED_PAID || status == PaymentPartStatus.VERIFIED) {
            notificationRepository?.addNotification(
                title = "Payment Marked as Paid",
                message = "Chunk payment part #$partId was marked as paid."
            )
        }
    }
}
