package com.footzone.footzone.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemPlayingPitchBinding
import com.footzone.footzone.helper.OnClickEventPlayingSoon
import com.footzone.footzone.model.PlayedHistoryResponseData
import com.footzone.footzone.model.PlayingSoonHistoryResponseData
import com.footzone.footzone.utils.commonfunction.Functions
import java.time.LocalTime

class PlayingPitchAdapter(private val onClickEventPlayingSoon: OnClickEventPlayingSoon) :
    RecyclerView.Adapter<PlayingPitchAdapter.VH>() {

    private val playedPitchList = ArrayList<PlayingSoonHistoryResponseData>()

    inner class VH(val view: ItemPlayingPitchBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPlayingPitchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val stadium = playedPitchList[position]
        val duration = Functions.calculateInHours(
            LocalTime.parse(stadium.startTime),
            LocalTime.parse(stadium.endTime)
        )
        holder.view.apply {
            tvPitchName.text =
                "${stadium.stadiumName} ${tvPitchName.context.getString(R.string.str_football_stadium)}"
            tvDate.text = stadium.startDate
            tvHours.text =
                "${stadium.startTime.subSequence(0, 5)}-${stadium.endTime.substring(0, 5)}," +
                        " $duration ${tvHours.context.getString(R.string.str_hour)}"
            tvPrice.text = "${stadium.hourlyPrice.toInt() * duration} so'm"
            if (stadium.status == "PENDING") {
                linearButtonWrapper.visibility = View.GONE
                tvStatus.visibility = View.VISIBLE
            } else {
                linearButtonWrapper.visibility = View.VISIBLE
                tvStatus.visibility = View.GONE

                btnNavigate.setOnClickListener {
                    onClickEventPlayingSoon.onNavigateClick(stadium.latitude, stadium.longitude)
                }

                btnStadium.setOnClickListener {
                    onClickEventPlayingSoon.onStadiumClick(stadium.stadiumId)
                }
            }
        }
    }

    override fun getItemCount(): Int = playedPitchList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(list: List<PlayingSoonHistoryResponseData>) {
        this.playedPitchList.clear()
        this.playedPitchList.addAll(list)
        notifyDataSetChanged()
    }
}