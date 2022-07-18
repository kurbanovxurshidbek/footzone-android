package com.footzone.footzone.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemUserNotificationBinding
import com.footzone.footzone.model.UserNotification

class UserNotificationAdapter(var items: ArrayList<UserNotification>) :
    RecyclerView.Adapter<UserNotificationAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemUserNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.view.apply {
            messageBody.text = item.body
            messageDate.text = item.date
            if (item.isChecked) {
                itemUserNotificationRoot.setBackgroundResource(R.drawable.checked_item_user_notification_background)
                messageBody.setTextColor(Color.parseColor("#424242"))
                messageDate.setTextColor(Color.parseColor("#424242"))
            } else {
                itemUserNotificationRoot.setBackgroundResource(R.drawable.unchecked_item_user_notification_background)
                messageBody.setTextColor(Color.parseColor("#0A0A0A"))
                messageDate.setTextColor(Color.parseColor("#0A0A0A"))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VH(val view: ItemUserNotificationBinding) : RecyclerView.ViewHolder(view.root)

}