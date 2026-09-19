package com.qrspliter.adil.presentation.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qrspliter.adil.databinding.ItemPaymentSessionBinding
import com.qrspliter.adil.domain.model.PaymentSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onItemClick: (PaymentSession) -> Unit,
    private val onSelectionChanged: (selectedCount: Int) -> Unit
) : ListAdapter<PaymentSession, HistoryAdapter.ViewHolder>(DiffCallback) {

    var isSelectionMode: Boolean = false
        private set

    val selectedSessionIds = HashSet<String>()

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) {
            selectedSessionIds.clear()
        }
        notifyDataSetChanged()
        onSelectionChanged(selectedSessionIds.size)
    }

    fun selectAll() {
        isSelectionMode = true
        selectedSessionIds.clear()
        currentList.forEach { selectedSessionIds.add(it.sessionId) }
        notifyDataSetChanged()
        onSelectionChanged(selectedSessionIds.size)
    }

    fun clearSelection() {
        selectedSessionIds.clear()
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPaymentSessionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)

        fun bind(session: PaymentSession) {
            binding.tvMerchantName.text = session.merchantName
            binding.tvDate.text = dateFormat.format(Date(session.createdAt))
            binding.tvTotalAmount.text = session.totalAmount.formattedRupees
            binding.tvPaidProgress.text = "${session.paidAmount.formattedRupees} paid / ${session.totalAmount.formattedRupees}"
            binding.tvPartsConfirmed.text = "${session.paidPartsCount} / ${session.totalPartsCount} parts confirmed"
            binding.chipSessionStatus.text = session.status.name

            // Unread / NEW badge
            if (!session.isRead) {
                binding.tvUnreadBadge.visibility = View.VISIBLE
            } else {
                binding.tvUnreadBadge.visibility = View.GONE
            }

            // Selection Checkbox
            if (isSelectionMode) {
                binding.chkSelectSession.visibility = View.VISIBLE
                binding.chkSelectSession.isChecked = selectedSessionIds.contains(session.sessionId)
            } else {
                binding.chkSelectSession.visibility = View.GONE
            }

            binding.chkSelectSession.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedSessionIds.add(session.sessionId)
                } else {
                    selectedSessionIds.remove(session.sessionId)
                }
                onSelectionChanged(selectedSessionIds.size)
            }

            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    val isCurrentlySelected = selectedSessionIds.contains(session.sessionId)
                    if (isCurrentlySelected) {
                        selectedSessionIds.remove(session.sessionId)
                    } else {
                        selectedSessionIds.add(session.sessionId)
                    }
                    notifyItemChanged(bindingAdapterPosition)
                    onSelectionChanged(selectedSessionIds.size)
                } else {
                    onItemClick(session)
                }
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<PaymentSession>() {
        override fun areItemsTheSame(oldItem: PaymentSession, newItem: PaymentSession): Boolean {
            return oldItem.sessionId == newItem.sessionId
        }

        override fun areContentsTheSame(oldItem: PaymentSession, newItem: PaymentSession): Boolean {
            return oldItem == newItem
        }
    }
}
