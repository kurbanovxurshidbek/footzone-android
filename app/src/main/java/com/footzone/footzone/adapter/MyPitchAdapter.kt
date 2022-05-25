package com.footzone.footzone.adapter

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemMyPitchLayoutBinding
import com.footzone.footzone.model.Pitch

class MyPitchAdapter(var context: Context, var pitches: ArrayList<Pitch>, private var onPitchClick: ((Pitch) -> Unit)):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ItemMyPitchLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPitchViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pitch = pitches[position]
        if (holder is MyPitchViewHolder){
            holder.view.apply {
                refreshImagesAdapter(pitch.images, rvPithPhotos)
                tvPitchName.text = pitch.name
                if (pitch.isOpen) {
                    tvOpenClose.text = Html.fromHtml("<font color=#177B4C>" + "Ochiq")
                    tvOpenCloseHour.text = " · ${pitch.time.closingTime} da yopiladi"
                } else {
                    tvOpenClose.text = Html.fromHtml("<font color=#C8303F>" + "Yopiq")
                    tvOpenCloseHour.text = " · ${pitch.time.openingTime} da ochiladi"
                }
                setStrokeColorToRatingBar(rbPitch)
                rbPitch.rating = pitch.rating
                rbPitch.setIsIndicator(true)
                tvRatingNums.text = "(${pitch.ratingNums})"
                tvPitchPrice.text = "${pitch.price} so'm/soat"

                btnManagement.setOnClickListener {
                    onPitchClick.invoke(pitch)
                }
            }
        }
    }

    private fun refreshImagesAdapter(images: ArrayList<String>, rvPithPhotos: RecyclerView) {
        val pitchImagesAdapter = PitchImagesAdapter()
        pitchImagesAdapter.submitData(images)
        rvPithPhotos.adapter = pitchImagesAdapter
    }

    override fun getItemCount(): Int {
        return pitches.size
    }

    class MyPitchViewHolder(val view: ItemMyPitchLayoutBinding) : RecyclerView.ViewHolder(view.root)
}