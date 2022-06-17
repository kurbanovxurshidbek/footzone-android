package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemPitchImageLayoutBinding
import com.footzone.footzone.utils.KeyValues.STADIUM_IMAGE_BASE_URL
import com.footzone.footzone.utils.commonfunction.Functions.loadImageUrl

/**
 * This adapter , user to view stadium images
 */
class PitchImagesAdapter() :
    RecyclerView.Adapter<PitchImagesAdapter.VH>() {

    var stadiumImages = ArrayList<String>()

    inner class VH(val binding: ItemPitchImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = stadiumImages[position]
        val uri = "${STADIUM_IMAGE_BASE_URL}${item}"
        //val uri = "http://192.168.43.32:8081/images/stadium/${item}"
        holder.binding.ivPitch.loadImageUrl(uri)
    }


    override fun getItemCount(): Int = stadiumImages.size

    fun submitData(stadiumImages: ArrayList<String>) {
        this.stadiumImages.addAll(stadiumImages)
    }
}