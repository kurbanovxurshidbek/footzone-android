package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemAdminNotificationBinding
import com.footzone.footzone.helper.OnClickEventAcceptDecline
import com.footzone.footzone.model.AdminNotification
import com.footzone.footzone.model.StadiumBookSentResponseData
import com.footzone.footzone.utils.extensions.hide

class AdminNotificationAdapter(
    var notifications: List<StadiumBookSentResponseData>,
    private val onClickEventAcceptDecline: OnClickEventAcceptDecline
) : RecyclerView.Adapter<AdminNotificationAdapter.VH>() {

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
        val notification = notifications[position]
        holder.view.apply {
            btnAccept.setOnClickListener {
                linearButtonWrapper.hide()
                onClickEventAcceptDecline.onAccept(notification.id, tvAccepted, acceptedLayout)
            }

            btnDecline.setOnClickListener {
                linearButtonWrapper.hide()
                onClickEventAcceptDecline.onDecline(notification.id, tvDeclined, declinedLayout)
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}