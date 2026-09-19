package com.qrspliter.adil.presentation.createpayment

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
import com.qrspliter.adil.R
import com.qrspliter.adil.UpiSplitterApplication
import com.qrspliter.adil.databinding.FragmentCreatePaymentBinding
import kotlinx.coroutines.launch

class CreatePaymentFragment : Fragment() {

    private var _binding: FragmentCreatePaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePaymentViewModel by viewModels {
        val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
        CreatePaymentViewModel.Factory(
            appContainer.createPaymentSessionUseCase,
            appContainer.settingsRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()

        binding.btnSubmit.setOnClickListener {
            val amount = binding.etAmount.text.toString().trim()
            val vpa = binding.etVpa.text.toString().trim()
            val name = binding.etMerchantName.text.toString().trim()
            val reference = binding.etReference.text.toString().trim().ifEmpty { null }
            val note = binding.etNote.text.toString().trim().ifEmpty { null }

            binding.tvErrorMessage.visibility = View.GONE

            viewModel.createPaymentSession(
                amount = amount,
                vpa = vpa,
                name = name,
                reference = reference,
                note = note
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.defaultMerchantInfo.collect { info ->
                        if (binding.etVpa.text.isNull_or_blank() && info.vpa.isNotEmpty()) {
                            binding.etVpa.setText(info.vpa)
                        }
                        if (binding.etMerchantName.text.isNull_or_blank() && info.name.isNotEmpty()) {
                            binding.etMerchantName.setText(info.name)
                        }
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is CreatePaymentState.Idle -> {
                                binding.btnSubmit.isEnabled = true
                            }
                            is CreatePaymentState.Loading -> {
                                binding.btnSubmit.isEnabled = false
                            }
                            is CreatePaymentState.Success -> {
                                binding.btnSubmit.isEnabled = true
                                viewModel.resetState()
                                val bundle = bundleOf(KEY_SESSION_ID to state.session.sessionId)
                                findNavController().navigate(
                                    R.id.action_createPaymentFragment_to_reviewSplitFragment,
                                    bundle
                                )
                            }
                            is CreatePaymentState.Error -> {
                                binding.btnSubmit.isEnabled = true
                                binding.tvErrorMessage.text = state.message
                                binding.tvErrorMessage.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun CharSequence?.isNull_or_blank(): Boolean = this == null || this.isBlank()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_SESSION_ID = "sessionId"
    }
}
