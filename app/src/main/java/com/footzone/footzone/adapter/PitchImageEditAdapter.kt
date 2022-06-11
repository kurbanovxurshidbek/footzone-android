package com.footzone.footzone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemStadiumImageEditBinding
import com.footzone.footzone.model.StadiumPhoto
import com.footzone.footzone.utils.KeyValues

class PitchImageEditAdapter(var context: Context, var pitchImages : ArrayList<StadiumPhoto>, private var onItemClicked: ((Int) -> Unit)) :
    RecyclerView.Adapter<PitchImageEditAdapter.VH>()  {

    inner class VH(val binding: ItemStadiumImageEditBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemStadiumImageEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(holder.binding.ivPitch)
            .load(pitchImages[position])
            .into(holder.binding.ivPitch)

        val uri = "${KeyValues.STADIUM_IMAGE_BASE_URL}${pitchImages[position].name}"
        Glide.with(holder.binding.ivPitch.context)
            .load(uri)
            .placeholder(R.drawable.stadim2)
            .into(holder.binding.ivPitch)

        holder.binding.apply {
            llConvert.setOnClickListener {
                onItemClicked.invoke(position)
            }

            llDelete.setOnClickListener {
                Toast.makeText(context, "Delete Image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = pitchImages.size
}