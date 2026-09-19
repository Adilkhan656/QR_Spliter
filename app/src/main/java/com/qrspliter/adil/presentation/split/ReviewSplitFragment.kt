package com.qrspliter.adil.presentation.split

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qrspliter.adil.R
import com.qrspliter.adil.UpiSplitterApplication
import com.qrspliter.adil.databinding.FragmentReviewSplitBinding
import com.qrspliter.adil.presentation.createpayment.CreatePaymentFragment
import kotlinx.coroutines.launch

class ReviewSplitFragment : Fragment() {

    private var _binding: FragmentReviewSplitBinding? = null
    private val binding get() = _binding!!

    private var sessionId: String = ""
    private lateinit var adapter: SplitChunkAdapter

    private val viewModel: ReviewSplitViewModel by viewModels {
        val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
        ReviewSplitViewModel.Factory(sessionId, appContainer.getPaymentSessionUseCase)
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
        _binding = FragmentReviewSplitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SplitChunkAdapter()
        binding.rvChunks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChunks.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sessionFlow.collect { session ->
                    session?.let {
                        binding.tvTotalAmount.text = it.totalAmount.formattedRupees
                        binding.tvMerchantDetails.text = "${it.merchantName} (${it.merchantVpa})"
                        binding.tvReference.text = "Ref: ${it.referenceId}"
                        adapter.submitList(it.parts)
                    }
                }
            }
        }

        binding.btnGenerateQrs.setOnClickListener {
            val bundle = bundleOf(
                CreatePaymentFragment.KEY_SESSION_ID to sessionId,
                KEY_PART_INDEX to 0
            )
            findNavController().navigate(
                R.id.action_reviewSplitFragment_to_paymentQrFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_PART_INDEX = "partIndex"
    }
}
