package com.footzone.footzone.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.databinding.ItemPitchImageLayoutBinding

class PitchImagesAdapter() :
    RecyclerView.Adapter<PitchImagesAdapter.VH>() {

    var pitchImages = ArrayList<String>()

    inner class VH(val binding: ItemPitchImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(holder.binding.ivPitch)
            .load(pitchImages[position])
            .into(holder.binding.ivPitch)
    }

    override fun getItemCount(): Int = pitchImages.size

    fun submitData(pitchImages: ArrayList<String>) {
        this.pitchImages.addAll(pitchImages)
    }
}