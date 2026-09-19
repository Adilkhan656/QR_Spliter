package com.qrspliter.adil.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.qrspliter.adil.UpiSplitterApplication
import com.qrspliter.adil.databinding.FragmentSettingsBinding
import com.qrspliter.adil.domain.model.DataRetentionPolicy
import com.qrspliter.adil.domain.model.MarkAsPaidConfirmationPolicy
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var selectedRetentionPolicy: DataRetentionPolicy = DataRetentionPolicy.NEVER
    private var selectedConfirmationPolicy: MarkAsPaidConfirmationPolicy = MarkAsPaidConfirmationPolicy.EVERY_TIME

    private val viewModel: SettingsViewModel by viewModels {
        val appContainer = (requireActivity().application as UpiSplitterApplication).appContainer
        SettingsViewModel.Factory(
            appContainer.settingsRepository,
            appContainer.paymentRepository,
            appContainer.saveSettingsUseCase
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRetentionPolicyDropdown()
        setupConfirmationPolicyDropdown()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.merchantInfo.collect { info ->
                        if (binding.etVpa.text.isNullOrEmpty()) {
                            binding.etVpa.setText(info.vpa)
                        }
                        if (binding.etName.text.isNullOrEmpty()) {
                            binding.etName.setText(info.name)
                        }
                    }
                }

                launch {
                    viewModel.maxChunkAmount.collect { money ->
                        if (binding.etMaxChunk.text.isNullOrEmpty()) {
                            binding.etMaxChunk.setText(money.inRupeesLong.toString())
                        }
                    }
                }

                launch {
                    viewModel.retentionPolicy.collect { policy ->
                        selectedRetentionPolicy = policy
                        binding.actRetentionPolicy.setText(policy.displayName, false)
                        updateCustomDaysVisibility(policy)
                    }
                }

                launch {
                    viewModel.confirmationPolicy.collect { policy ->
                        selectedConfirmationPolicy = policy
                        binding.actConfirmationPolicy.setText(policy.displayName, false)
                    }
                }

                launch {
                    viewModel.customDays.collect { days ->
                        if (binding.etCustomDays.text.isNullOrEmpty()) {
                            binding.etCustomDays.setText(days.toString())
                        }
                    }
                }

                launch {
                    viewModel.saveState.collect { state ->
                        when (state) {
                            is SaveSettingsState.Idle -> {}
                            is SaveSettingsState.Success -> {
                                Toast.makeText(requireContext(), "Settings saved successfully", Toast.LENGTH_SHORT).show()
                            }
                            is SaveSettingsState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val vpa = binding.etVpa.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val chunk = binding.etMaxChunk.text.toString().trim()
            val customDays = binding.etCustomDays.text.toString().trim()

            viewModel.saveSettings(vpa, name, chunk, selectedRetentionPolicy, selectedConfirmationPolicy, customDays)
        }

        binding.btnClearHistory.setOnClickListener {
            showClearHistoryConfirmation()
        }
    }

    private fun setupRetentionPolicyDropdown() {
        val policies = DataRetentionPolicy.entries
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            policies.map { it.displayName }
        )

        binding.actRetentionPolicy.setAdapter(adapter)
        binding.actRetentionPolicy.setOnItemClickListener { _, _, position, _ ->
            selectedRetentionPolicy = policies[position]
            updateCustomDaysVisibility(selectedRetentionPolicy)
        }
    }

    private fun setupConfirmationPolicyDropdown() {
        val policies = MarkAsPaidConfirmationPolicy.entries
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            policies.map { it.displayName }
        )

        binding.actConfirmationPolicy.setAdapter(adapter)
        binding.actConfirmationPolicy.setOnItemClickListener { _, _, position, _ ->
            selectedConfirmationPolicy = policies[position]
        }
    }

    private fun updateCustomDaysVisibility(policy: DataRetentionPolicy) {
        if (policy == DataRetentionPolicy.CUSTOM_DAYS) {
            binding.tilCustomDays.visibility = View.VISIBLE
        } else {
            binding.tilCustomDays.visibility = View.GONE
        }
    }

    private fun showClearHistoryConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear Payment History")
            .setMessage("Are you sure you want to delete all saved payment session history? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                viewModel.clearAllHistory()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
