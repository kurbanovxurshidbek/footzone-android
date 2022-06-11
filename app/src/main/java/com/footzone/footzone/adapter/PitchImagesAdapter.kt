package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemPitchImageLayoutBinding
import com.footzone.footzone.model.holderstadium.Photo
import com.footzone.footzone.utils.KeyValues

class PitchImagesAdapter() :
    RecyclerView.Adapter<PitchImagesAdapter.VH>() {

    var stadiumImages = ArrayList<Photo>()

    inner class VH(val binding: ItemPitchImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = stadiumImages[position]
        val uri = "${KeyValues.STADIUM_IMAGE_BASE_URL}${item.name}"
        Glide.with(holder.binding.ivPitch.context)
            .load(uri)
            .placeholder(R.drawable.stadim2)
            .into(holder.binding.ivPitch)
    }

    override fun getItemCount(): Int = stadiumImages.size

    fun submitData(stadiumImages: List<Photo>) {
        this.stadiumImages.addAll(stadiumImages)
        notifyDataSetChanged()
    }
}