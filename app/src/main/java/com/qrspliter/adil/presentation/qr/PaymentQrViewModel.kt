package com.qrspliter.adil.presentation.qr

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qrspliter.adil.core.qr.QrCodeGenerator
import com.qrspliter.adil.domain.model.PaymentPart
import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.usecase.GetPaymentSessionUseCase
import com.qrspliter.adil.domain.usecase.UpdatePaymentPartStatusUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QrUiState {
    object Loading : QrUiState()
    data class Content(
        val session: PaymentSession,
        val currentPartIndex: Int,
        val currentPart: PaymentPart,
        val qrBitmap: Bitmap,
        val totalParts: Int,
        val isLastPart: Boolean
    ) : QrUiState()
    data class SessionFinished(val sessionId: String) : QrUiState()
    data class Error(val message: String) : QrUiState()
}

class PaymentQrViewModel(
    private val sessionId: String,
    initialPartIndex: Int,
    private val getPaymentSessionUseCase: GetPaymentSessionUseCase,
    private val updatePaymentPartStatusUseCase: UpdatePaymentPartStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<QrUiState>(QrUiState.Loading)
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private var currentPartIndex = initialPartIndex
    private var timerJob: Job? = null

    init {
        loadSession()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        _remainingSeconds.value = 60
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000L)
                _remainingSeconds.value -= 1
            }
        }
    }

    fun restartTimer() {
        startTimer()
    }

    private fun loadSession() {
        viewModelScope.launch {
            getPaymentSessionUseCase.observeSession(sessionId).collect { session ->
                if (session == null) {
                    _uiState.value = QrUiState.Error("Payment session not found")
                    return@collect
                }

                if (currentPartIndex >= session.parts.size) {
                    _uiState.value = QrUiState.SessionFinished(sessionId)
                    return@collect
                }

                val currentPart = session.parts[currentPartIndex]
                val bitmap = try {
                    QrCodeGenerator.generateQrBitmap(currentPart.generatedUri)
                } catch (e: Exception) {
                    _uiState.value = QrUiState.Error("Failed to generate QR: ${e.message}")
                    return@collect
                }

                _uiState.value = QrUiState.Content(
                    session = session,
                    currentPartIndex = currentPartIndex,
                    currentPart = currentPart,
                    qrBitmap = bitmap,
                    totalParts = session.parts.size,
                    isLastPart = currentPartIndex == session.parts.size - 1
                )
            }
        }
    }

    fun markCurrentPartAsPaid() {
        val currentState = _uiState.value as? QrUiState.Content ?: return
        viewModelScope.launch {
            updatePaymentPartStatusUseCase(
                partId = currentState.currentPart.partId,
                status = PaymentPartStatus.USER_REPORTED_PAID,
                paidAt = System.currentTimeMillis()
            )
            nextPart()
        }
    }

    fun nextPart() {
        val currentState = _uiState.value as? QrUiState.Content ?: return
        if (currentState.isLastPart) {
            _uiState.value = QrUiState.SessionFinished(sessionId)
        } else {
            currentPartIndex++
            startTimer()
            loadSession()
        }
    }

    fun previousPart() {
        if (currentPartIndex > 0) {
            currentPartIndex--
            startTimer()
            loadSession()
        }
    }

    class Factory(
        private val sessionId: String,
        private val initialPartIndex: Int,
        private val getPaymentSessionUseCase: GetPaymentSessionUseCase,
        private val updatePaymentPartStatusUseCase: UpdatePaymentPartStatusUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentQrViewModel(
                sessionId,
                initialPartIndex,
                getPaymentSessionUseCase,
                updatePaymentPartStatusUseCase
            ) as T
        }
    }
}
