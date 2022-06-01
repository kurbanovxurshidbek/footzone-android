package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.databinding.ItemAddImageBinding
import com.footzone.footzone.databinding.ItemStadiumImageBinding
import com.footzone.footzone.model.Image
import com.footzone.footzone.ui.fragments.addstadium.AddStadiumFragment


class AddImageAdapter(
    val context: AddStadiumFragment,
    val items: ArrayList<Image>,
    private var onItemClicked: ((Int) -> Unit),
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_ITEM_DEFAULT_IMAGE = 1
    private val TYPE_ITEM_NEW_IMAGE = 2

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_ITEM_DEFAULT_IMAGE
        }else {
            TYPE_ITEM_NEW_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM_DEFAULT_IMAGE){
            val view = ItemAddImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DefualtImageViewHolder(view)
        }

        val view = ItemStadiumImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is DefualtImageViewHolder){
            holder.view.apply {
                ivAddImage.setOnClickListener {
                    onItemClicked.invoke(position)
                }
            }
        }

        if (holder is NewImageViewHolder){
            if (item.imageUri != null){
                Glide.with(holder.view.ivPitch)
                    .load(item.imageUri)
                    .into(holder.view.ivPitch)
                holder.view.ivCaution.setOnClickListener {
                    onItemClicked.invoke(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    }

    class DefualtImageViewHolder(val view: ItemAddImageBinding) : RecyclerView.ViewHolder(view.root)

    class NewImageViewHolder(val view: ItemStadiumImageBinding) : RecyclerView.ViewHolder(view.root) {
}