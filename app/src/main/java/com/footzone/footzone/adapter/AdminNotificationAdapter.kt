package com.footzone.footzone.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemAdminNotificationBinding
import com.footzone.footzone.helper.OnClickEventAcceptDecline
import com.footzone.footzone.model.AdminNotification
import com.footzone.footzone.model.StadiumBookSentResponseData
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.extensions.hide
import com.footzone.footzone.utils.extensions.show
import java.time.LocalTime

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val notification = notifications[position]

        val duration = Functions.calculateInHours(
            LocalTime.parse(notification.startTime),
            LocalTime.parse(notification.endTime)
        )
        holder.view.apply {
            tvStadiumName.text =
                "${notification.stadiumName} ${tvStadiumName.context.getText(R.string.str_football_stadium)}"
            tvDate.text = notification.startDate
            tvHours.text =
                "${notification.startTime.subSequence(0, 5)}-${
                    notification.endTime.substring(
                        0,
                        5
                    )
                }, " +
                        "$duration ${tvHours.context.getText(R.string.str_hour)}"
            tvPrice.text = "${(notification.hourlyPrice.toInt() * duration).toInt()} so'm"
            "${notification.startTime.subSequence(0, 5)}-${
                notification.endTime.substring(
                    0,
                    5
                )
            }, $duration soat"
            tvPrice.text = "${notification.hourlyPrice.toInt() * duration} so'm"

            if (notification.status == "ACCEPTED") {
                linearButtonWrapper.hide()
                acceptedLayout.show()
                declinedLayout.hide()
            }
            if (notification.status == "DECLINED") {
                linearButtonWrapper.hide()
                declinedLayout.show()
                acceptedLayout.hide()
            }

            if (notification.status == "PENDING") {
                linearButtonWrapper.show()
                acceptedLayout.hide()
                declinedLayout.hide()
            }

            btnAccept.setOnClickListener {
                linearButtonWrapper.hide()
                onClickEventAcceptDecline.onAccept(
                    notification.id,
                    tvAccepted,
                    acceptedLayout,
                    position
                )
            }

            btnDecline.setOnClickListener {
                linearButtonWrapper.hide()
                onClickEventAcceptDecline.onDecline(
                    notification.id,
                    tvDeclined,
                    declinedLayout,
                    position
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun changeNotificationStatus(status: String, position: Int) {
        notifications[position].status = status
        notifyItemChanged(position)
    }
}