package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemPitchImageLayoutBinding

class PitchImagesAdapter() :
    RecyclerView.Adapter<PitchImagesAdapter.VH>() {

    var stadiumImages = ArrayList<String>()

    inner class VH(val binding: ItemPitchImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = stadiumImages[position]
       // val uri = "https://footzone-server.herokuapp.com/images/stadium/${item}"
        val uri = "http://192.168.43.32:8081/images/stadium/${item}"
        Glide.with(holder.binding.ivPitch.context)
            .load(uri)
            .placeholder(R.drawable.stadim2)
            .into(holder.binding.ivPitch)
    }

    override fun getItemCount(): Int = stadiumImages.size

    fun submitData(stadiumImages: ArrayList<String>) {
        this.stadiumImages.addAll(stadiumImages)
    }
}