package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemAddImageBinding
import com.footzone.footzone.databinding.ItemStadiumImageEditBinding
import com.footzone.footzone.helper.OnClickEditEvent
import com.footzone.footzone.model.EditPhoto
import com.footzone.footzone.model.StadiumPhoto
import com.footzone.footzone.utils.KeyValues

class PitchImageEditAdapter(var pitchImages : ArrayList<EditPhoto>,   private var onClickEditEvent: OnClickEditEvent) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private val TYPE_ITEM_DEFAULT_IMAGE = 1
    private val TYPE_ITEM_EDIT_IMAGE = 2

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_ITEM_DEFAULT_IMAGE
        }else {
            TYPE_ITEM_EDIT_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM_DEFAULT_IMAGE){
            val view = ItemAddImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AddImageViewHolder(view)
        }

        val view = ItemStadiumImageEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = pitchImages[position]
        if (holder is AddImageViewHolder){
            holder.view.apply {
                ivAddImage.setOnClickListener {
                    onClickEditEvent.setOnAddClickListener()
                }
            }
        }

        if (holder is EditImageViewHolder){
            Glide.with(holder.view.ivPitch)
                .load(pitchImages[position])
                .into(holder.view.ivPitch)

            val uri = "${KeyValues.STADIUM_IMAGE_BASE_URL}${pitchImages[position].name}"
            Glide.with(holder.view.ivPitch.context)
                .load(uri)
                .placeholder(R.drawable.stadim2)
                .into(holder.view.ivPitch)

            holder.view.apply {
                llConvert.setOnClickListener {
                    onClickEditEvent.setOnEditClickListener(position, item.id!!)
                }

                llDelete.setOnClickListener {
                    onClickEditEvent.setOnDeleteClickListener(position, item.id!!)
                }
            }
        }
    }

    override fun getItemCount(): Int = pitchImages.size
}


class AddImageViewHolder(val view: ItemAddImageBinding) : RecyclerView.ViewHolder(view.root)

class EditImageViewHolder(val view: ItemStadiumImageEditBinding) : RecyclerView.ViewHolder(view.root)