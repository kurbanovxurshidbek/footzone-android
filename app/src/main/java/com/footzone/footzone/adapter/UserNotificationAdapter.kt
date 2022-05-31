package com.footzone.footzone.adapter

import android.annotation.SuppressLint
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
            messageBody.setText(item.body)
            messageDate.setText(item.date)
            if (item.isChecked){
                itemUserNotificationRoot.setBackgroundResource(R.drawable.checked_item_user_notification_background)
                messageBody.setTextColor(R.color.lightTextColor)
            } else {
                itemUserNotificationRoot.setBackgroundResource(R.drawable.unchecked_item_user_notification_background)
                messageBody.setTextColor(R.color.darkTextColor)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VH(val view: ItemUserNotificationBinding) : RecyclerView.ViewHolder(view.root)

}