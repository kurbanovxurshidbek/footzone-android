package com.footzone.footzone.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemMyPitchLayoutBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.ShortStadiumDetail
import com.footzone.footzone.utils.commonfunction.Functions.resRating
import com.footzone.footzone.utils.commonfunction.Functions.showStadiumOpenOrClose

/**
 * This is adapter, the user to see a list of stadiums
 */

class HolderPitchAdapter(
    var items: ArrayList<ShortStadiumDetail>,
    var onClickEvent: OnClickEvent,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            ItemMyPitchLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPitchViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]

        if (holder is MyPitchViewHolder) {
            holder.view.apply {
                refreshImagesAdapter(data.photos, rvPithPhotos)
                tvPitchName.text = data.name
                showStadiumOpenOrClose(tvOpenClose, tvOpenCloseHour, data.isOpen)
                setStrokeColorToRatingBar(rbPitch)
                rbPitch.rating = resRating(data.comments)
                rbPitch.setIsIndicator(true)
                tvRatingNums.text = "(${data.comments.size})"
                tvPitchPrice.text = "${data.hourlyPrice} ${tvPitchPrice.context.getText(R.string.str_so_m_soat)}"

                btnManagement.setOnClickListener {
                    onClickEvent.setOnBookClickListener(data.stadiumId, false)
                }

                linearNavigation.setOnClickListener {
                    onClickEvent.setOnNavigateClickListener(data.latitude, data.longitude)
                }
            }
        }
    }

    private fun refreshImagesAdapter(photos: ArrayList<String>, rvPithPhotos: RecyclerView) {
        val pitchImagesAdapter = PitchImagesAdapter()
        pitchImagesAdapter.submitData(photos)
        rvPithPhotos.adapter = pitchImagesAdapter
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MyPitchViewHolder(val view: ItemMyPitchLayoutBinding) : RecyclerView.ViewHolder(view.root)
}

