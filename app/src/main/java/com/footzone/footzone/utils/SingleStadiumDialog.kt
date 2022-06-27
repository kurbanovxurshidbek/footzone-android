package com.footzone.footzone.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PitchImagesAdapter
import com.footzone.footzone.adapter.setStrokeColorToRatingBar
import com.footzone.footzone.databinding.ItemPitchLayoutBinding
import com.footzone.footzone.databinding.ItemSingleStadiumDataBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.ShortStadiumDetail
import com.footzone.footzone.model.SingleStadiumResponse
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.commonfunction.Functions.setFavouriteBackground

class SingleStadiumDialog(
    private val isFavourite: Boolean,
    private val shortStadiumDetail: ShortStadiumDetail,
    private val context1: Context,
    private val onClickEvent: OnClickEvent
) : Dialog(context1) {
    private var _instance: SingleStadiumDialog? = null

    override fun onBackPressed() {
        super.onBackPressed()
        this._instance!!.dismiss()
    }

    fun instance(layoutBinding: ItemSingleStadiumDataBinding): SingleStadiumDialog {
        if (_instance == null) {
            _instance = this
        }
        setLayout(layoutBinding)
        return _instance!!
    }

    private fun setLayout(layoutBinding: ItemSingleStadiumDataBinding) {
        _instance!!.setContentView(layoutBinding.root)
        _instance!!.window!!.setLayout(
            pxFromDp(context1, 350).toInt(),
            pxFromDp(context1, 325).toInt()
        )
        _instance!!.window!!.setBackgroundDrawableResource(R.drawable.rounded_view)
        _instance!!.window!!.attributes.gravity = Gravity.BOTTOM
        _instance!!.window!!.attributes.y = 164

        layoutBinding.apply {

            refreshImagesAdapter(shortStadiumDetail.photos, rvPithPhotos)
            tvPitchName.text = shortStadiumDetail.name
            Functions.showStadiumOpenOrClose(
                tvOpenClose,
                tvOpenCloseHour,
                shortStadiumDetail.isOpen
            )
            setStrokeColorToRatingBar(rbPitch)
            rbPitch.rating = Functions.resRating(shortStadiumDetail.comments)
            rbPitch.setIsIndicator(true)
            tvRatingNums.text = "(${shortStadiumDetail.comments.sumBy { it.number }})"
            tvPitchPrice.text =
                "${shortStadiumDetail.hourlyPrice} ${tvPitchPrice.context.getText(R.string.str_so_m_soat)}"
            tvRatingNums.text = "(${shortStadiumDetail.comments.sumOf { it.number }})"
            tvPitchPrice.text = "${shortStadiumDetail.hourlyPrice} so'm/soat"

            if (isFavourite) {
                ivBookmark.setFavouriteBackground()
            }

            ivBookmark.setOnClickListener {
                onClickEvent.setOnBookMarkClickListener(
                    shortStadiumDetail.stadiumId,
                    layoutBinding.ivBookmark
                )
            }
            btnBook.setOnClickListener {
                _instance!!.dismiss()
                onClickEvent.setOnBookClickListener(shortStadiumDetail.stadiumId, isFavourite)
            }
            btnNavigate.setOnClickListener {
                onClickEvent.setOnNavigateClickListener(
                    shortStadiumDetail.latitude,
                    shortStadiumDetail.longitude
                )
            }
        }
    }

    private fun pxFromDp(context: Context, dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun refreshImagesAdapter(images: ArrayList<String>, rvPithPhotos: RecyclerView) {
        val pitchImagesAdapter = PitchImagesAdapter()
        pitchImagesAdapter.submitData(images)
        rvPithPhotos.adapter = pitchImagesAdapter
    }
}