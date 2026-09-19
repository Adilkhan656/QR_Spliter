package com.qrspliter.adil.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.MarkAsPaidConfirmationPolicy
import com.qrspliter.adil.domain.model.Money
import timber.log.Timber

class SecurityPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to initialize EncryptedSharedPreferences, falling back to standard private preferences")
        context.getSharedPreferences(PREFS_FILENAME_FALLBACK, Context.MODE_PRIVATE)
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted() {
        sharedPreferences.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
    }

    fun getDefaultVpa(): String {
        return sharedPreferences.getString(KEY_DEFAULT_VPA, "") ?: ""
    }

    fun setDefaultVpa(vpa: String) {
        sharedPreferences.edit().putString(KEY_DEFAULT_VPA, vpa.trim()).apply()
    }

    fun getDefaultMerchantName(): String {
        return sharedPreferences.getString(KEY_DEFAULT_MERCHANT_NAME, "") ?: ""
    }

    fun setDefaultMerchantName(name: String) {
        sharedPreferences.edit().putString(KEY_DEFAULT_MERCHANT_NAME, name.trim()).apply()
    }

    fun getMaxChunkAmount(): Money {
        val paise = sharedPreferences.getLong(KEY_MAX_CHUNK_PAISE, DEFAULT_MAX_CHUNK_PAISE)
        return Money(paise)
    }

    fun setMaxChunkAmount(amount: Money) {
        sharedPreferences.edit().putLong(KEY_MAX_CHUNK_PAISE, amount.paise).apply()
    }

    fun getDataRetentionPolicy(): DataRetentionPolicy {
        val name = sharedPreferences.getString(KEY_RETENTION_POLICY, DataRetentionPolicy.NEVER.name)
        return DataRetentionPolicy.fromName(name)
    }

    fun setDataRetentionPolicy(policy: DataRetentionPolicy) {
        sharedPreferences.edit().putString(KEY_RETENTION_POLICY, policy.name).apply()
    }

    fun getCustomRetentionDays(): Int {
        return sharedPreferences.getInt(KEY_CUSTOM_RETENTION_DAYS, 7)
    }

    fun setCustomRetentionDays(days: Int) {
        sharedPreferences.edit().putInt(KEY_CUSTOM_RETENTION_DAYS, days.coerceAtLeast(1)).apply()
    }

    fun getConfirmationPolicy(): MarkAsPaidConfirmationPolicy {
        val name = sharedPreferences.getString(KEY_CONFIRMATION_POLICY, MarkAsPaidConfirmationPolicy.EVERY_TIME.name)
        return MarkAsPaidConfirmationPolicy.fromName(name)
    }

    fun setConfirmationPolicy(policy: MarkAsPaidConfirmationPolicy) {
        sharedPreferences.edit().putString(KEY_CONFIRMATION_POLICY, policy.name).apply()
    }

    companion object {
        private const val PREFS_FILENAME = "upi_splitter_secure_prefs"
        private const val PREFS_FILENAME_FALLBACK = "upi_splitter_private_prefs"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_DEFAULT_VPA = "default_vpa"
        private const val KEY_DEFAULT_MERCHANT_NAME = "default_merchant_name"
        private const val KEY_MAX_CHUNK_PAISE = "max_chunk_paise"
        private const val KEY_RETENTION_POLICY = "data_retention_policy"
        private const val KEY_CUSTOM_RETENTION_DAYS = "custom_retention_days"
        private const val KEY_CONFIRMATION_POLICY = "mark_as_paid_confirmation_policy"
        const val DEFAULT_MAX_CHUNK_PAISE = 199900L // ₹1,999
    }
}
