package com.footzone.footzone.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemPitchBookSentBinding
import com.footzone.footzone.helper.OnClickEventAcceptDecline
import com.footzone.footzone.model.StadiumBookSentResponseData
import com.footzone.footzone.utils.commonfunction.Functions.calculateInHours
import java.time.LocalTime

class PitchBookSentAdapter(
    private val playedPitchList: List<StadiumBookSentResponseData>,
    private val onClickEventAcceptDecline: OnClickEventAcceptDecline
) :
    RecyclerView.Adapter<PitchBookSentAdapter.VH>() {

    inner class VH(val view: ItemPitchBookSentBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchBookSentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val stadium = playedPitchList[position]
        val duration = calculateInHours(
            LocalTime.parse(stadium.startTime),
            LocalTime.parse(stadium.endTime)
        )
        holder.view.apply {
            tvPitchName.text =
                "${stadium.stadiumName} ${tvPitchName.context.getText(R.string.str_football_stadium)}"
            tvDate.text = stadium.date
            tvHours.text =
                "${stadium.startTime.subSequence(0, 5)}-${stadium.endTime.substring(0, 5)}, " +
                        "$duration ${tvHours.context.getText(R.string.str_hour)}"
            tvPrice.text = "${(stadium.hourlyPrice.toInt()*duration).toInt()} so'm"
                "${stadium.startTime.subSequence(0, 5)}-${
                    stadium.endTime.substring(
                        0,
                        5
                    )
                }, $duration soat"
            tvPrice.text = "${stadium.hourlyPrice.toInt() * duration} so'm"

            btnAccept.setOnClickListener {
                onClickEventAcceptDecline.onAccept(stadium.sessionId,tvStatus,linearAcceptDecline)
            }

            btnDecline.setOnClickListener {
                onClickEventAcceptDecline.onDecline(stadium.sessionId,tvStatus,linearAcceptDecline)
            }
        }
    }

    override fun getItemCount(): Int = playedPitchList.size
}