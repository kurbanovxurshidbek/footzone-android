package com.footzone.footzone.adapter

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemAddImageBinding
import com.footzone.footzone.databinding.ItemStadiumImageBinding
import com.footzone.footzone.model.Image
import com.footzone.footzone.ui.fragments.AddStadiumFragment


class AddImageAdapter(
    val context: AddStadiumFragment,
    val items: ArrayList<Image>,
    private var onItemClicked: (() -> Unit),
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_ITEM_DEFAULT_IMAGE = 1
    private val TYPE_ITEM_NEW_IMAGE = 2

    override fun getItemViewType(position: Int): Int {
        val item =items[position]
        return if (position == items.size - 1) {
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
        val item = items[items.size - 1 - position]
        if (holder is DefualtImageViewHolder){
            holder.view.apply {
                ivAddImage.setOnClickListener {
                    onItemClicked.invoke()
                }
            }
        }

        if (holder is NewImageViewHolder){
            if (item.imagePath != null){
                holder.view.ivPitch.setImageURI(item.imagePath)
                Log.d("##$$##", item.imagePath.toString())

            }
            Log.d("##$$##", item.imagePath.toString())
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    }

    class DefualtImageViewHolder(val view: ItemAddImageBinding) : RecyclerView.ViewHolder(view.root)

    class NewImageViewHolder(val view: ItemStadiumImageBinding) : RecyclerView.ViewHolder(view.root) {
}