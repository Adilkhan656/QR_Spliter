package com.qrspliter.adil.presentation.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qrspliter.adil.databinding.ItemPaymentPartBinding
import com.qrspliter.adil.domain.model.PaymentPart

class PaymentPartAdapter(
    private val onPartClick: ((PaymentPart) -> Unit)? = null
) : ListAdapter<PaymentPart, PaymentPartAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentPartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onPartClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemPaymentPartBinding,
        private val onPartClick: ((PaymentPart) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(part: PaymentPart) {
            binding.tvPartSequence.text = part.sequenceNumber.toString()
            binding.tvPartAmount.text = part.amount.formattedRupees
            binding.tvPartRef.text = part.clientReference
            binding.chipPartStatus.text = part.status.name

            binding.root.setOnClickListener {
                onPartClick?.invoke(part)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<PaymentPart>() {
        override fun areItemsTheSame(oldItem: PaymentPart, newItem: PaymentPart): Boolean {
            return oldItem.partId == newItem.partId
        }

        override fun areContentsTheSame(oldItem: PaymentPart, newItem: PaymentPart): Boolean {
            return oldItem == newItem
        }
    }
}
