package com.qrspliter.adil.presentation.complete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qrspliter.adil.MainActivity
import com.qrspliter.adil.R
import com.qrspliter.adil.UpiSplitterApplication
import com.qrspliter.adil.databinding.FragmentPaymentCompleteBinding
import com.qrspliter.adil.domain.model.PaymentSession
import com.qrspliter.adil.domain.model.PaymentSessionStatus
import com.qrspliter.adil.presentation.createpayment.CreatePaymentFragment
import com.qrspliter.adil.presentation.details.PaymentPartAdapter
import com.qrspliter.adil.presentation.split.ReviewSplitFragment
import kotlinx.coroutines.launch

class PaymentCompleteFragment : Fragment() {

    private var _binding: FragmentPaymentCompleteBinding? = null
    private val binding get() = _binding!!

    private var sessionId: String = ""
    private var currentSession: PaymentSession? = null
    private lateinit var adapter: PaymentPartAdapter

    private val viewModel: PaymentCompleteViewModel by viewModels {
        val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
        PaymentCompleteViewModel.Factory(
            sessionId,
            appContainer.getPaymentSessionUseCase,
            appContainer.updatePaymentPartStatusUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionId = arguments?.getString(CreatePaymentFragment.KEY_SESSION_ID) ?: ""

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PaymentPartAdapter()
        binding.rvParts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParts.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sessionFlow.collect { session ->
                    session?.let {
                        currentSession = it
                        binding.tvTotalAmount.text = it.totalAmount.formattedRupees
                        binding.tvPaidSummary.text = "${it.paidAmount.formattedRupees} / ${it.totalAmount.formattedRupees} Paid (${it.paidPartsCount} of ${it.totalPartsCount} parts)"
                        binding.tvSessionStatusTitle.text = "Session Status: ${it.status.name}"
                        adapter.submitList(it.parts)

                        val animRes = when (it.status) {
                            PaymentSessionStatus.PAID -> R.raw.success_animation
                            PaymentSessionStatus.CANCELLED -> R.raw.failed
                            else -> R.raw.pending_animation
                        }
                        binding.lottieAnimation.apply {
                            cancelAnimation()
                            setAnimation(animRes)
                            playAnimation()
                        }
                        binding.btnDone.text = if (it.isResumable) {
                            "Resume latest QR"
                        } else {
                            "Back to Home"
                        }
                        // Set Top App Bar Subtitle with Session Status
                        (activity as? MainActivity)?.setToolbarSubtitle("Status: ${it.status.name}")
                    }
                }
            }
        }

        binding.btnDone.setOnClickListener {
            navigateNext()
        }

        binding.btnCancelSession.setOnClickListener {
            showCancelConfirmation()
        }
    }

    private fun navigateNext() {
        val session = currentSession
        if (session != null && session.isResumable) {
            val lastPartIndex = (session.parts.size - 1).coerceAtLeast(0)
            val bundle = bundleOf(
                CreatePaymentFragment.KEY_SESSION_ID to session.sessionId,
                ReviewSplitFragment.KEY_PART_INDEX to lastPartIndex
            )
            findNavController().navigate(
                R.id.action_paymentCompleteFragment_to_paymentQrFragment,
                bundle
            )
        } else {
            findNavController().navigate(R.id.action_paymentCompleteFragment_to_homeFragment)
        }
    }

    fun handleBackNavigation() {
        navigateNext()
    }

    private fun showCancelConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel / Refund Session")
            .setMessage("Are you sure you want to mark this payment session as Cancelled / Refunded?")
            .setPositiveButton("Confirm Cancel") { _, _ ->
                viewModel.cancelPaymentSession()
                Toast.makeText(requireContext(), "Payment session marked as Cancelled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Back", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val PaymentSession.isResumable: Boolean
        get() = status == PaymentSessionStatus.PENDING || status == PaymentSessionStatus.PARTIALLY_PAID
}
