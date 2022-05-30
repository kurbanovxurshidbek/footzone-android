package com.footzone.footzone.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.databinding.ItemStadiumImageEditBinding

class PitchImageEditAdapter( var context: Context, var pitchImages : ArrayList<String>,  private var onItemClicked: ((Int) -> Unit)) :
    RecyclerView.Adapter<PitchImageEditAdapter.VH>()  {



    inner class VH(val binding: ItemStadiumImageEditBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemStadiumImageEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(holder.binding.ivPitch)
            .load(pitchImages[position])
            .into(holder.binding.ivPitch)

      //  Log.d("@@@###", pitchImages[position])

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