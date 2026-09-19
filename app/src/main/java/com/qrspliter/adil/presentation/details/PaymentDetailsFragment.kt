package com.qrspliter.adil.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.qrspliter.adil.databinding.FragmentPaymentDetailsBinding
import com.qrspliter.adil.presentation.createpayment.CreatePaymentFragment
import com.qrspliter.adil.presentation.split.ReviewSplitFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentDetailsFragment : Fragment() {

    private var _binding: FragmentPaymentDetailsBinding? = null
    private val binding get() = _binding!!

    private var sessionId: String = ""
    private lateinit var adapter: PaymentPartAdapter
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)

    private val viewModel: PaymentDetailsViewModel by viewModels {
        val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
        PaymentDetailsViewModel.Factory(
            sessionId,
            appContainer.getPaymentSessionUseCase,
            appContainer.paymentRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionId = arguments?.getString(CreatePaymentFragment.KEY_SESSION_ID) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PaymentPartAdapter { part ->
            val bundle = bundleOf(
                CreatePaymentFragment.KEY_SESSION_ID to sessionId,
                ReviewSplitFragment.KEY_PART_INDEX to (part.sequenceNumber - 1)
            )
            findNavController().navigate(
                R.id.action_paymentDetailsFragment_to_paymentQrFragment,
                bundle
            )
        }

        binding.rvParts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParts.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sessionFlow.collect { session ->
                    session?.let {
                        binding.tvMerchantName.text = it.merchantName
                        binding.chipStatus.text = it.status.displayName
                        binding.tvTotalAmount.text = it.totalAmount.formattedRupees
                        binding.tvPaidProgress.text = "Paid: ${it.paidAmount.formattedRupees} / Remaining: ${it.remainingAmount.formattedRupees}"
                        binding.tvVpa.text = "UPI ID: ${it.merchantVpa}"
                        binding.tvReference.text = "Ref: ${it.referenceId}"
                        binding.tvDate.text = "Created: ${dateFormat.format(Date(it.createdAt))}"
                        adapter.submitList(it.parts)

                        // Set Top App Bar Subtitle with Session Status
                        (activity as? MainActivity)?.setToolbarSubtitle("Status: ${it.status.displayName}")
                    }
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Session")
            .setMessage("Are you sure you want to delete this payment session?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteSession()
                findNavController().navigateUp()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
