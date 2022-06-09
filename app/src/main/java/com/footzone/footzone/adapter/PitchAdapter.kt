package com.footzone.footzone.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemPitchLayoutBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.model.holders.Photo
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class PitchAdapter(
    private var onClickEvent: OnClickEvent
) :
    RecyclerView.Adapter<PitchAdapter.VH>() {

    private var pitches = ArrayList<Pitch>()

    inner class VH(val view: ItemPitchLayoutBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pitch = pitches[position]
        holder.view.apply {
            // refreshImagesAdapter(pitch.images, rvPithPhotos)
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

            btnNavigate.setOnClickListener {
                onClickEvent.setOnNavigateClickListener(1.0, 2.0)
            }

            ivBookmark.setOnClickListener {
                onClickEvent.setOnBookMarkClickListener(
                    "8c0c9599-90c0-43c9-a5cf-ffe1765a35e5",
                    pitch.name!!,
                    ivBookmark
                )
            }

            btnBook.setOnClickListener {
                onClickEvent.setOnBookClickListener(pitch.id)
            }
        }
    }

    private fun refreshImagesAdapter(images: ArrayList<Photo>, rvPithPhotos: RecyclerView) {
        val pitchImagesAdapter = PitchImagesAdapter()
        pitchImagesAdapter.submitData(images as ArrayList<Photo>)
        rvPithPhotos.adapter = pitchImagesAdapter
    }

    override fun getItemCount(): Int = pitches.size

    fun submitData(pitches: ArrayList<Pitch>) {
        this.pitches.addAll(pitches)
    }
}

fun setStrokeColorToRatingBar(ratingBar: MaterialRatingBar) {
    val stars = ratingBar.progressDrawable as LayerDrawable
    stars.getDrawable(2).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
    stars.getDrawable(0).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
    stars.getDrawable(1).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
}
