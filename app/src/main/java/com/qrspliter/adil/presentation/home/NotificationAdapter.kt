package com.qrspliter.adil.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qrspliter.adil.databinding.ItemNotificationBinding
import com.qrspliter.adil.domain.model.AppNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter : ListAdapter<AppNotification, NotificationAdapter.ViewHolder>(DiffCallback) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: AppNotification) {
            binding.tvNotificationTitle.text = notification.title
            binding.tvNotificationMessage.text = notification.message
            binding.tvNotificationTime.text = dateFormat.format(Date(notification.timestamp))
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<AppNotification>() {
        override fun areItemsTheSame(oldItem: AppNotification, newItem: AppNotification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppNotification, newItem: AppNotification): Boolean {
            return oldItem == newItem
        }
    }
}
