package com.qrspliter.adil.presentation.split

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qrspliter.adil.databinding.ItemSplitChunkBinding
import com.qrspliter.adil.domain.model.PaymentPart

class SplitChunkAdapter : ListAdapter<PaymentPart, SplitChunkAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSplitChunkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSplitChunkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(part: PaymentPart) {
            binding.tvPartSequence.text = part.sequenceNumber.toString()
            binding.tvPartAmount.text = part.amount.formattedRupees
            binding.tvPartReference.text = part.clientReference
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
