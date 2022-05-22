package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemPlayingPitchBinding
import com.footzone.footzone.model.PitchHistory

class PlayingPitchAdapter :
    RecyclerView.Adapter<PlayingPitchAdapter.VH>() {

    private val playedPitchList = ArrayList<PitchHistory>()

    inner class VH(val view: ItemPlayingPitchBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPlayingPitchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pitch = playedPitchList[position]
        holder.view.apply {
            tvPitchName.text = "${pitch.name} futbol maydoni"
            tvDate.text = pitch.date
            tvHours.text = "${pitch.hour.start}-${pitch.hour.end}, 2 soat"
            tvPrice.text = "${pitch.price} so'm"
        }
    }

    override fun getItemCount(): Int = playedPitchList.size

    fun submitData(list: ArrayList<PitchHistory>) {
        this.playedPitchList.addAll(list)
        notifyDataSetChanged()
    }
}