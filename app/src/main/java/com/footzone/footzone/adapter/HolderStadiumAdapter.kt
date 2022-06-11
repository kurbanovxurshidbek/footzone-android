package com.footzone.footzone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemRvBinding
import com.footzone.footzone.model.holderpitch.Photo

class HolderStadiumAdapter(var items: ArrayList<Photo>) :
    RecyclerView.Adapter<HolderStadiumAdapter.VH>() {


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
            val uri = "https://footzone-server.herokuapp.com/images/stadium/${item.name}"

            Log.d("TAG", "onBindViewHolder: ${uri}")
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