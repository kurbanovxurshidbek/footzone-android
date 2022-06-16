package com.footzone.footzone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemRvBinding
import com.footzone.footzone.model.StadiumPhoto
import com.footzone.footzone.utils.KeyValues

class CustomAdapter(var items: ArrayList<StadiumPhoto>) :
    RecyclerView.Adapter<CustomAdapter.VH>() {


    inner class VH(val view: ItemRvBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            ItemRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.view.apply {
            val uri = "${KeyValues.STADIUM_IMAGE_BASE_URL}${item.name}"
            Glide.with(itemImageView.context)
                .load(uri)
                .placeholder(R.drawable.stadim2)
                .into(itemImageView)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}