package com.footzone.footzone.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemMyPitchLayoutBinding
import com.footzone.footzone.helper.OnClickEvent
import com.footzone.footzone.model.Comment
import com.footzone.footzone.model.ShortStadiumDetail
import com.footzone.footzone.utils.commonfunction.Functions
import com.footzone.footzone.utils.commonfunction.Functions.showStadiumOpenOrClose
import java.lang.Exception

class MyPitchAdapter(
    var items: ArrayList<ShortStadiumDetail>,
    var onClickEvent: OnClickEvent
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            ItemMyPitchLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPitchViewHolder(view)
    }

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
                tvPitchPrice.text = "${data.hourlyPrice} so'm/soat"

                btnManagement.setOnClickListener {
                    onClickEvent.setOnBookClickListener(data.stadiumId,false)
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

fun resRating(comments: ArrayList<Comment>): Float {
    return try {
        (comments.sumOf { it.number * it.rate } / comments.sumOf { it.number }).toFloat()
    } catch (e: Exception) {
        2.5f
    }
}