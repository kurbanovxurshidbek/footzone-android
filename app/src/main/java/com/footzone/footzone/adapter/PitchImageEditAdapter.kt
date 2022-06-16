package com.footzone.footzone.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.databinding.ItemAddImageBinding
import com.footzone.footzone.databinding.ItemStadiumImageEditBinding
import com.footzone.footzone.helper.OnClickEditEvent
import com.footzone.footzone.model.EditPhoto
import com.footzone.footzone.utils.KeyValues
import java.util.*

/**
 * This adapter, user can edit stadium images
 */
class PitchImageEditAdapter(var pitchImages: LinkedList<EditPhoto>, private var onClickEditEvent: OnClickEditEvent) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val TYPE_ITEM_DEFAULT_IMAGE = 1001
    private val TYPE_ITEM_EDIT_IMAGE = 1002

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
        when(holder) {
            is AddImageViewHolder -> {
                holder.view.apply {
                    ivAddImage.setOnClickListener {
                        onClickEditEvent.setOnAddClickListener()
                    }
                }
            }

            is EditImageViewHolder -> {
                if (pitchImages[position].name is String) {
                    val uri = "${KeyValues.STADIUM_IMAGE_BASE_URL}${pitchImages[position].name}"
                    //val uri = "http://192.168.43.32:8081/images/stadium/${pitchImages[position].name}"
                    Glide.with(holder.view.ivPitch.context)
                        .load(uri)
                        .into(holder.view.ivPitch)
                } else if (pitchImages[position].name is Uri) {
                    holder.view.ivPitch.setImageURI(pitchImages[position].name as Uri)
                }
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
    }

    override fun getItemCount(): Int = pitchImages.size

    class AddImageViewHolder(val view: ItemAddImageBinding) : RecyclerView.ViewHolder(view.root)

    class EditImageViewHolder(val view: ItemStadiumImageEditBinding) : RecyclerView.ViewHolder(view.root)

}
