package com.qrspliter.adil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qrspliter.adil.databinding.ActivityMainBinding
import com.qrspliter.adil.presentation.complete.PaymentCompleteFragment
import com.qrspliter.adil.presentation.home.NotificationAdapter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment)
        )

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.toolbar.subtitle = null
        }

        setupNotificationBell()
    }

    private fun setupNotificationBell() {
        val appContainer = (application as UpiSplitterApplication).appContainer

        lifecycleScope.launch {
            appContainer.notificationRepository.observeUnreadCount().collect { count ->
                if (count > 0) {
                    binding.tvNotificationBadge.visibility = View.VISIBLE
                    binding.tvNotificationBadge.text = count.toString()
                } else {
                    binding.tvNotificationBadge.visibility = View.GONE
                }
            }
        }

        binding.layoutNotificationBell.setOnClickListener {
            showNotificationsDialog()
        }
    }

    private fun showNotificationsDialog() {
        val appContainer = (application as UpiSplitterApplication).appContainer
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notifications, null)
        val rvNotifications = dialogView.findViewById<RecyclerView>(R.id.rvNotifications)
        val btnClear = dialogView.findViewById<Button>(R.id.btnClearNotifications)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseNotifications)

        val adapter = NotificationAdapter()
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        lifecycleScope.launch {
            appContainer.notificationRepository.markAllNotificationsRead()
            val list = appContainer.notificationRepository.observeAllNotifications().firstOrNull() ?: emptyList()
            adapter.submitList(list)
        }

        btnClear.setOnClickListener {
            lifecycleScope.launch {
                appContainer.notificationRepository.clearAllNotifications()
                adapter.submitList(emptyList())
            }
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setToolbarSubtitle(subtitle: String?) {
        binding.toolbar.subtitle = subtitle
    }

    override fun onSupportNavigateUp(): Boolean {
        val currentFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
            ?.childFragmentManager
            ?.primaryNavigationFragment

        if (currentFragment is PaymentCompleteFragment) {
            currentFragment.handleBackNavigation()
            return true
        }

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
