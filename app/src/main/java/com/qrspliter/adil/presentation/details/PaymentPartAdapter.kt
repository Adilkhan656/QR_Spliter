package com.qrspliter.adil.presentation.details

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qrspliter.adil.R
import com.qrspliter.adil.databinding.ItemPaymentPartBinding
import com.qrspliter.adil.domain.model.PaymentPart
import com.qrspliter.adil.domain.model.PaymentPartStatus

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
            val context = binding.root.context
            binding.tvPartSequence.text = part.sequenceNumber.toString()
            binding.tvPartAmount.text = part.amount.formattedRupees
            binding.tvPartRef.text = part.clientReference

            val (fgColorRes, bgColorRes) = when (part.status) {
                PaymentPartStatus.USER_REPORTED_PAID, PaymentPartStatus.VERIFIED -> R.color.status_paid_fg to R.color.status_paid_bg
                PaymentPartStatus.CANCELLED, PaymentPartStatus.FAILED -> R.color.status_cancelled_fg to R.color.status_cancelled_bg
                else -> R.color.status_pending_fg to R.color.status_pending_bg
            }

            binding.chipPartStatus.apply {
                text = part.status.displayName
                setTextColor(ContextCompat.getColor(context, fgColorRes))
                chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, bgColorRes))
                chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, fgColorRes))
            }

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
