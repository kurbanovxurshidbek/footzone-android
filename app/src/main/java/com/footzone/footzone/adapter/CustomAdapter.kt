package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemRvBinding
import com.footzone.footzone.model.StadiumPhoto
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.commonfunction.Functions.loadImageUrl

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
            itemImageView.loadImageUrl(uri)
        }
    }

    override fun getItemCount(): Int = items.size
}