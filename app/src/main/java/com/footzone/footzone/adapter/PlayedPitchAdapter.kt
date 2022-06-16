package com.footzone.footzone.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemPlayedPitchBinding
import com.footzone.footzone.model.PitchHistory
import com.footzone.footzone.model.PlayedHistoryResponseData
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.commonfunction.Functions.calculateInHours
import java.time.LocalTime

class PlayedPitchAdapter :
    RecyclerView.Adapter<PlayedPitchAdapter.VH>() {

    private val playedPitchList = ArrayList<PlayedHistoryResponseData>()

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
            tvDate.text = stadium.startDate
            tvHours.text =
                "${stadium.startTime.subSequence(0, 5)}-${stadium.endTime.substring(0, 5)}, $duration soat"
            tvPrice.text = "${stadium.hourlyPrice.toInt()*duration} so'm"
        }
    }

    override fun getItemCount(): Int = playedPitchList.size

    fun submitData(list: List<PlayedHistoryResponseData>) {
        this.playedPitchList.addAll(list)
        notifyDataSetChanged()
    }
}