package com.footzone.footzone.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.databinding.ItemRvBinding

class CustomAdapter(var items: ArrayList<String>) :
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
            Glide.with(itemImageView)
                .load(item[position])
                .into(itemImageView)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}