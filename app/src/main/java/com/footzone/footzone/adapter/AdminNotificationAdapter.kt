package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemAdminNotificationBinding
import com.footzone.footzone.model.AdminNotification

class AdminNotificationAdapter(var items: ArrayList<AdminNotification>): RecyclerView.Adapter<AdminNotificationAdapter.VH>() {

    inner class VH(val view: ItemAdminNotificationBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemAdminNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.view.apply {

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}