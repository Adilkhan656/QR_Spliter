package com.qrspliter.adil.core.util

import android.content.Context
import com.qrspliter.adil.core.security.SecurityPreferences
import com.qrspliter.adil.data.local.database.AppDatabase
import com.qrspliter.adil.data.repository.PaymentRepositoryImpl
import com.qrspliter.adil.data.repository.SettingsRepositoryImpl
import com.qrspliter.adil.domain.repository.PaymentRepository
import com.qrspliter.adil.domain.repository.SettingsRepository
import com.qrspliter.adil.domain.usecase.CreatePaymentSessionUseCase
import com.qrspliter.adil.domain.usecase.GetPaymentHistoryUseCase
import com.qrspliter.adil.domain.usecase.GetPaymentSessionUseCase
import com.qrspliter.adil.domain.usecase.SaveSettingsUseCase
import com.qrspliter.adil.domain.usecase.UpdatePaymentPartStatusUseCase

class AppContainer(context: Context) {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    val securityPreferences: SecurityPreferences by lazy {
        SecurityPreferences(context)
    }

    val paymentRepository: PaymentRepository by lazy {
        PaymentRepositoryImpl(
            sessionDao = database.paymentSessionDao(),
            partDao = database.paymentPartDao()
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(securityPreferences)
    }

    val createPaymentSessionUseCase: CreatePaymentSessionUseCase by lazy {
        CreatePaymentSessionUseCase(paymentRepository, settingsRepository)
    }

    val getPaymentSessionUseCase: GetPaymentSessionUseCase by lazy {
        GetPaymentSessionUseCase(paymentRepository)
    }

    val getPaymentHistoryUseCase: GetPaymentHistoryUseCase by lazy {
        GetPaymentHistoryUseCase(paymentRepository)
    }

    val updatePaymentPartStatusUseCase: UpdatePaymentPartStatusUseCase by lazy {
        UpdatePaymentPartStatusUseCase(paymentRepository)
    }

    val saveSettingsUseCase: SaveSettingsUseCase by lazy {
        SaveSettingsUseCase(settingsRepository)
    }
}
