package com.qrspliter.adil.presentation.qr

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.qrspliter.adil.MainActivity
import com.qrspliter.adil.R
import com.qrspliter.adil.UpiSplitterApplication
import com.qrspliter.adil.databinding.FragmentPaymentQrBinding
import com.qrspliter.adil.domain.model.MarkAsPaidConfirmationPolicy
import com.qrspliter.adil.domain.model.PaymentPartStatus
import com.qrspliter.adil.presentation.createpayment.CreatePaymentFragment
import com.qrspliter.adil.presentation.split.ReviewSplitFragment
import kotlinx.coroutines.launch
import java.util.Locale

class PaymentQrFragment : Fragment() {

    private var _binding: FragmentPaymentQrBinding? = null
    private val binding get() = _binding!!

    private var sessionId: String = ""
    private var partIndex: Int = 0

    private val viewModel: PaymentQrViewModel by viewModels {
        val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
        PaymentQrViewModel.Factory(
            sessionId,
            partIndex,
            appContainer.getPaymentSessionUseCase,
            appContainer.updatePaymentPartStatusUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionId = arguments?.getString(CreatePaymentFragment.KEY_SESSION_ID) ?: ""
        partIndex = arguments?.getInt(ReviewSplitFragment.KEY_PART_INDEX, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()

        binding.btnMarkAsPaid.setOnClickListener {
            handleMarkAsPaid()
        }

        binding.btnNextPayment.setOnClickListener {
            viewModel.nextPart()
        }

        binding.btnPreviousPayment.setOnClickListener {
            viewModel.previousPart()
        }

        binding.btnRefreshQr.setOnClickListener {
            viewModel.restartTimer()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun handleMarkAsPaid() {
        lifecycleScope.launch {
            val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
            val policy = appContainer.settingsRepository.getConfirmationPolicy()

            when (policy) {
                MarkAsPaidConfirmationPolicy.NEVER -> {
                    viewModel.markCurrentPartAsPaid()
                }
                MarkAsPaidConfirmationPolicy.ONCE_PER_SESSION -> {
                    if (confirmedSessions.contains(sessionId)) {
                        viewModel.markCurrentPartAsPaid()
                    } else {
                        showConfirmationDialog()
                    }
                }
                MarkAsPaidConfirmationPolicy.EVERY_TIME -> {
                    showConfirmationDialog()
                }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.remainingSeconds.collect { seconds ->
                        if (seconds > 0) {
                            binding.tvQrTimer.text = String.format(Locale.US, "QR valid for 0:%02d", seconds)
                            binding.ivQrCode.alpha = 1.0f
                            binding.btnRefreshQr.visibility = View.GONE
                        } else {
                            binding.tvQrTimer.text = "QR Expired / Invalid"
                            binding.ivQrCode.alpha = 0.2f
                            binding.btnRefreshQr.visibility = View.VISIBLE
                        }
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is QrUiState.Loading -> {
                                // Loading view
                            }
                            is QrUiState.Content -> {
                                val part = state.currentPart
                                binding.tvStepIndicator.text = getString(
                                    R.string.qr_step_format,
                                    state.currentPartIndex + 1,
                                    state.totalParts
                                )
                                binding.tvPartAmount.text = part.amount.formattedRupees
                                binding.tvMerchantDetails.text = "Merchant: ${state.session.merchantName} (${state.session.merchantVpa})"
                                binding.tvReference.text = "Ref: ${part.clientReference}"
                                binding.ivQrCode.setImageBitmap(state.qrBitmap)

                                val (fgColorRes, bgColorRes) = when (part.status) {
                                    PaymentPartStatus.USER_REPORTED_PAID, PaymentPartStatus.VERIFIED -> R.color.status_paid_fg to R.color.status_paid_bg
                                    PaymentPartStatus.CANCELLED, PaymentPartStatus.FAILED -> R.color.status_cancelled_fg to R.color.status_cancelled_bg
                                    else -> R.color.status_pending_fg to R.color.status_pending_bg
                                }

                                binding.chipStatus.apply {
                                    text = part.status.displayName
                                    setTextColor(ContextCompat.getColor(requireContext(), fgColorRes))
                                    chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bgColorRes))
                                    chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), fgColorRes))
                                }

                                // Set Top App Bar Subtitle with Session Status
                                (activity as? MainActivity)?.setToolbarSubtitle("Status: ${state.session.status.displayName}")

                                binding.cardQr.setOnClickListener {
                                    showEnlargedQrDialog(part.amount.formattedRupees, state.qrBitmap)
                                }

                                binding.btnOpenUpiApp.setOnClickListener {
                                    launchUpiApp(part.generatedUri)
                                }

                                if (state.currentPartIndex > 0) {
                                    binding.btnPreviousPayment.visibility = View.VISIBLE
                                } else {
                                    binding.btnPreviousPayment.visibility = View.GONE
                                }

                                if (state.isLastPart && part.status == PaymentPartStatus.USER_REPORTED_PAID) {
                                    binding.btnNextPayment.text = "Complete Payment"
                                } else {
                                    binding.btnNextPayment.text = getString(R.string.btn_next_payment)
                                }
                            }
                            is QrUiState.SessionFinished -> {
                                val bundle = bundleOf(CreatePaymentFragment.KEY_SESSION_ID to state.sessionId)
                                findNavController().navigate(
                                    R.id.action_paymentQrFragment_to_paymentCompleteFragment,
                                    bundle
                                )
                            }
                            is QrUiState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun launchUpiApp(upiUriString: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUriString))
            val chooser = Intent.createChooser(intent, "Pay with UPI Application")
            startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No compatible UPI app found on device", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to launch UPI app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.warning_mark_paid_title)
            .setMessage(R.string.warning_mark_paid_message)
            .setPositiveButton(R.string.btn_confirm_paid) { _, _ ->
                confirmedSessions.add(sessionId)
                viewModel.markCurrentPartAsPaid()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEnlargedQrDialog(amountText: String, qrBitmap: Bitmap) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_qr_enlarge, null)
        val tvAmount = dialogView.findViewById<TextView>(R.id.tvEnlargedAmount)
        val ivQr = dialogView.findViewById<ImageView>(R.id.ivEnlargedQr)

        tvAmount.text = amountText
        ivQr.setImageBitmap(qrBitmap)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnCloseEnlarged).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val confirmedSessions = HashSet<String>()
    }
}
