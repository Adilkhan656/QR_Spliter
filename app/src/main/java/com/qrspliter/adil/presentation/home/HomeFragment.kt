package com.qrspliter.adil.presentation.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.qrspliter.adil.R
import com.qrspliter.adil.UpiSplitterApplication
import com.qrspliter.adil.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val appContainer =
            (requireActivity().application as UpiSplitterApplication).appContainer

        HomeViewModel.Factory(
            appContainer.getPaymentHistoryUseCase
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAndShowFirstTimeGuide()

        binding.btnCreatePayment.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_createPaymentFragment
            )
        }

        binding.btnHistory.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_historyFragment
            )
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_settingsFragment
            )
        }

        binding.btnAbout.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_aboutFragment
            )
        }
    }

    private fun checkAndShowFirstTimeGuide() {
        val appContainer =
            (requireActivity().application as UpiSplitterApplication).appContainer

        if (!appContainer.securityPreferences.isFirstLaunch()) {
            return
        }

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_first_time_guide, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<View>(R.id.btnProceedGuide)
            .setOnClickListener {
                appContainer.securityPreferences.setFirstLaunchCompleted()
                dialog.dismiss()
            }

        dialog.show()

        // Make the dialog itself transparent so only the rounded card is visible.
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}