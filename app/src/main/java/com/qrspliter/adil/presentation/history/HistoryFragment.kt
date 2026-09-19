package com.qrspliter.adil.presentation.history

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
import com.qrspliter.adil.databinding.FragmentHistoryBinding
import com.qrspliter.adil.presentation.createpayment.CreatePaymentFragment
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistoryAdapter

    private val viewModel: HistoryViewModel by viewModels {
        val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
        HistoryViewModel.Factory(
            appContainer.getPaymentHistoryUseCase,
            appContainer.paymentRepository,
            appContainer.settingsRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter { session ->
            val bundle = bundleOf(CreatePaymentFragment.KEY_SESSION_ID to session.sessionId)
            findNavController().navigate(
                R.id.action_historyFragment_to_paymentDetailsFragment,
                bundle
            )
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyFlow.collect { list ->
                    if (list.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvHistory.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvHistory.visibility = View.VISIBLE
                        adapter.submitList(list)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        viewModel.onHistoryViewClosed()
        super.onDestroyView()
        _binding = null
    }
}
