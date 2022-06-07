package com.footzone.footzone.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.databinding.ItemPitchImageLayoutBinding
import com.footzone.footzone.model.holders.Photo

class PitchImagesAdapter() :
    RecyclerView.Adapter<PitchImagesAdapter.VH>() {

    var stadiumImages = ArrayList<Photo>()

    inner class VH(val binding: ItemPitchImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPitchImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = stadiumImages[position]
        val uri = "http://10.10.2.18:8081/images/stadium/${item.name}"
        Glide.with(holder.binding.ivPitch)
            .load(uri)
            .into(holder.binding.ivPitch)
    }

    override fun getItemCount(): Int = stadiumImages.size

    fun submitData(stadiumImages: ArrayList<Photo>) {
        this.stadiumImages.addAll(stadiumImages)
    }
}