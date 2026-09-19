package com.qrspliter.adil

import android.app.Application
import com.qrspliter.adil.core.util.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class UpiSplitterApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Perform background data retention cleanup on app startup
        applicationScope.launch {
            try {
                val policy = appContainer.settingsRepository.getDataRetentionPolicy()
                val customDays = appContainer.settingsRepository.getCustomRetentionDays()
                appContainer.paymentRepository.cleanupExpiredSessions(policy, customDays)
            } catch (e: Exception) {
                Timber.e(e, "Error performing startup retention cleanup")
            }
        }
    }
}
