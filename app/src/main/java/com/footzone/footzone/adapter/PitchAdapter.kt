package com.footzone.footzone.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemPitchLayoutBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.ShortStadiumDetail
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.commonfunction.Functions.setFavouriteBackground
import com.footzone.footzone.utils.commonfunction.Functions.showStadiumOpenOrClose
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class PitchAdapter(
    private val favouriteStadiums: List<String>,
    private val pitches: ArrayList<ShortStadiumDetail>,
    private var onClickEvent: OnClickEvent,
) :
    RecyclerView.Adapter<PitchAdapter.VH>() {

    inner class VH(val view: ItemPitchLayoutBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val pitch = pitches[position]
        holder.view.apply {
            refreshImagesAdapter(pitch.photos, rvPithPhotos)
            tvPitchName.text = pitch.name
            showStadiumOpenOrClose(tvOpenClose, tvOpenCloseHour, pitch.isOpen)
            setStrokeColorToRatingBar(rbPitch)
            rbPitch.rating = Functions.resRating(pitch.comments)
            rbPitch.setIsIndicator(true)
            tvRatingNums.text = "(${pitch.comments.sumBy { it.number }})"
            tvPitchPrice.text =
                "${pitch.hourlyPrice} ${tvPitchPrice.context.getText(R.string.str_so_m_soat)}"

            if (favouriteStadiums.contains(pitch.stadiumId)) {
                ivBookmark.setFavouriteBackground()
            }

            btnNavigate.setOnClickListener {
                onClickEvent.setOnNavigateClickListener(1.0, 2.0)
            }

            ivBookmark.setOnClickListener {
                onClickEvent.setOnBookMarkClickListener(
                    pitch.stadiumId,
                    ivBookmark
                )
            }

            btnBook.setOnClickListener {
                onClickEvent.setOnBookClickListener(
                    pitch.stadiumId,
                    favouriteStadiums.contains(pitch.stadiumId)
                )
            }
        }
    }

    private fun refreshImagesAdapter(images: ArrayList<String>, rvPithPhotos: RecyclerView) {
        val pitchImagesAdapter = PitchImagesAdapter()
        pitchImagesAdapter.submitData(images)
        rvPithPhotos.adapter = pitchImagesAdapter
    }

    override fun getItemCount(): Int = pitches.size
}

fun setStrokeColorToRatingBar(ratingBar: MaterialRatingBar) {
    val stars = ratingBar.progressDrawable as LayerDrawable
    stars.getDrawable(2).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
    stars.getDrawable(0).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
    stars.getDrawable(1).setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_ATOP)
}