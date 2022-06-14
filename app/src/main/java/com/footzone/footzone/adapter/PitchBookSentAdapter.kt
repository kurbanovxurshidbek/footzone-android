package com.footzone.footzone.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemPitchBookSentBinding
import com.footzone.footzone.helper.OnClickEventAcceptDecline
import com.footzone.footzone.model.StadiumBookSentResponse
import com.footzone.footzone.model.StadiumBookSentResponseData
import java.time.Duration
import java.time.LocalTime

class PitchBookSentAdapter(
    private val playedPitchList: ArrayList<StadiumBookSentResponseData>,
    private val onClickEventAcceptDecline: OnClickEventAcceptDecline
) :
    RecyclerView.Adapter<PitchBookSentAdapter.VH>() {

    inner class VH(val view: ItemPitchBookSentBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchBookSentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val stadium = playedPitchList[position]
        holder.view.apply {
            tvPitchName.text = "${stadium.stadiumName} futbol maydoni"
            tvDate.text = stadium.date.toString()
            tvHours.text = "${stadium.startTime}-${stadium.endTime}, ${
                calculateInHours(
                    stadium.startTime,
                    stadium.endTime
                )
            }"
            tvPrice.text = "${stadium.hourlyPrice.toInt()} so'm"

            btnAccept.setOnClickListener {
                onClickEventAcceptDecline.onAccept()
            }

            btnDecline.setOnClickListener {
                onClickEventAcceptDecline.onDecline()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateInHours(startTime: LocalTime, endTime: LocalTime): String {
        return Duration.between(startTime, endTime).toHours().toString()
    }

    override fun getItemCount(): Int = playedPitchList.size
}