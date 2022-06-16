package com.footzone.footzone.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemPlayedPitchBinding
import com.footzone.footzone.model.StadiumBookSentResponseData
import com.footzone.footzone.utils.commonfunction.Functions.calculateInHours
import java.time.LocalTime

class StadiumPlayedHistoryAdapter :
    RecyclerView.Adapter<StadiumPlayedHistoryAdapter.VH>() {

    private val playedPitchList = ArrayList<StadiumBookSentResponseData>()

    inner class VH(val view: ItemPlayedPitchBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPlayedPitchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val stadium = playedPitchList[position]
        val duration = calculateInHours(
            LocalTime.parse(stadium.startTime),
            LocalTime.parse(stadium.endTime)
        )
        holder.view.apply {
            tvPitchName.text = "${stadium.stadiumName} futbol maydoni"
            tvDate.text = stadium.date
            tvHours.text =
                "${stadium.startTime.subSequence(0, 5)}-${
                    stadium.endTime.substring(
                        0,
                        5
                    )
                }, $duration soat"
            tvPrice.text = "${stadium.hourlyPrice.toInt() * duration} so'm"
        }
    }

    override fun getItemCount(): Int = playedPitchList.size

    fun submitData(list: List<StadiumBookSentResponseData>) {
        this.playedPitchList.addAll(list)
        notifyDataSetChanged()
    }
}