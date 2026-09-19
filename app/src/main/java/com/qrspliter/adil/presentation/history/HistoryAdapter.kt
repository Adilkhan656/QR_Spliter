package com.qrspliter.adil.presentation.history

import android.view.LayoutInflater
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
    private val onItemClick: (PaymentSession) -> Unit
) : ListAdapter<PaymentSession, HistoryAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemPaymentSessionBinding,
        private val onItemClick: (PaymentSession) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)

        fun bind(session: PaymentSession) {
            binding.tvMerchantName.text = session.merchantName
            binding.tvDate.text = dateFormat.format(Date(session.createdAt))
            binding.tvTotalAmount.text = session.totalAmount.formattedRupees
            binding.tvPaidProgress.text = "${session.paidAmount.formattedRupees} paid / ${session.totalAmount.formattedRupees}"
            binding.tvPartsConfirmed.text = "${session.paidPartsCount} / ${session.totalPartsCount} parts confirmed"
            binding.chipSessionStatus.text = session.status.name

            binding.root.setOnClickListener {
                onItemClick(session)
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
