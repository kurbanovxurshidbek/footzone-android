package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemPitchBookSentBinding
import com.footzone.footzone.model.PitchHistory

class PitchBookSentAdapter(val onRespondClick: (Boolean, String) -> Unit) :
    RecyclerView.Adapter<PitchBookSentAdapter.VH>() {

    private val playedPitchList = ArrayList<PitchHistory>()

    inner class VH(val view: ItemPitchBookSentBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchBookSentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pitch = playedPitchList[position]
        holder.view.apply {
            tvPitchName.text = "${pitch.name} futbol maydoni"
            tvDate.text = pitch.date
            tvHours.text = "${pitch.hour.start}-${pitch.hour.end}, 2 soat"
            tvPrice.text = "${pitch.price} so'm"

            btnAccept.setOnClickListener {
                onRespondClick.invoke(true, "stadiumId")
            }

            btnDecline.setOnClickListener {
                onRespondClick.invoke(false, "stadiumId")
            }
        }
    }

    override fun getItemCount(): Int = playedPitchList.size

    fun submitData(list: ArrayList<PitchHistory>) {
        this.playedPitchList.addAll(list)
        notifyDataSetChanged()
    }
}