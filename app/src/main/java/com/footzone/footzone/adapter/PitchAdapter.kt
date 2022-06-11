package com.footzone.footzone.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemPitchLayoutBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.StadiumData
import com.footzone.footzone.model.holders.Comment
import com.footzone.footzone.model.holderstadium.Photo
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.lang.Exception

class PitchAdapter(
    private var onClickEvent: OnClickEvent
) :
    RecyclerView.Adapter<PitchAdapter.VH>() {

    private var pitches = ArrayList<StadiumData>()

    inner class VH(val view: ItemPitchLayoutBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pitch = pitches[position]
        holder.view.apply {
            refreshImagesAdapter(pitch.photos, rvPithPhotos)
            tvPitchName.text = pitch.name
            if (pitch.isOpen.open) {
                tvOpenClose.text = Html.fromHtml("<font color=#177B4C>" + "Ochiq")
                tvOpenCloseHour.text = " · ${pitch.isOpen.time} da yopiladi"
            } else {
                tvOpenClose.text = Html.fromHtml("<font color=#C8303F>" + "Yopiq")
                tvOpenCloseHour.text = " · ${pitch.isOpen.time} da ochiladi"
            }
            setStrokeColorToRatingBar(rbPitch)
            rbPitch.rating = resRating(pitch.comments)
            rbPitch.setIsIndicator(true)
            tvRatingNums.text = "(${pitch.comments.size})"
            tvPitchPrice.text = "${pitch.hourlyPrice} so'm/soat"

            btnNavigate.setOnClickListener {
                onClickEvent.setOnNavigateClickListener(1.0, 2.0)
            }

            ivBookmark.setOnClickListener {
                onClickEvent.setOnBookMarkClickListener(
                    pitch.stadiumId,
                    pitch.name,
                    ivBookmark
                )
            }

            btnBook.setOnClickListener {
                onClickEvent.setOnBookClickListener(pitch.stadiumId)
            }
        }
    }

    private fun refreshImagesAdapter(images: List<Photo>, rvPithPhotos: RecyclerView) {
        val pitchImagesAdapter = PitchImagesAdapter()
        pitchImagesAdapter.submitData(images)
        rvPithPhotos.adapter = pitchImagesAdapter
    }

    override fun getItemCount(): Int = pitches.size

    fun submitData(pitches: ArrayList<StadiumData>) {
        this.pitches.addAll(pitches)
        notifyDataSetChanged()
    }
}

fun setStrokeColorToRatingBar(ratingBar: MaterialRatingBar) {
    val stars = ratingBar.progressDrawable as LayerDrawable
    stars.getDrawable(2).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
    stars.getDrawable(0).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
    stars.getDrawable(1).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
}

fun resRating(comments: ArrayList<Comment>): Float {
    return try {
        (comments.sumOf { it.number * it.rate } / comments.sumOf { it.number }).toFloat()
    } catch (e: Exception) {
        2.5f
    }
}
