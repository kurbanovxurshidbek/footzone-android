package com.footzone.footzone.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemAddImageBinding
import com.footzone.footzone.databinding.ItemStadiumImageEditBinding
import com.footzone.footzone.helper.OnClickEditEvent
import com.footzone.footzone.model.EditPhoto
import com.footzone.footzone.model.StadiumPhoto
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.utils.KeyValues

class PitchImageEditAdapter(var pitchImages : ArrayList<EditPhoto>,   private var onClickEditEvent: OnClickEditEvent) :
   // RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    ListAdapter<EditPhoto, RecyclerView.ViewHolder>(DiffUtil()){
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
            return ViewHolder.AddImageViewHolder(view)
        }

        val view = ItemStadiumImageEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder.EditImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = pitchImages[position]
        when(holder) {
           is ViewHolder.AddImageViewHolder -> {
                holder.view.apply {
                    ivAddImage.setOnClickListener {
                        onClickEditEvent.setOnAddClickListener()
                    }
                }
            }

            is ViewHolder.EditImageViewHolder -> {

                if (pitchImages[position].name is String) {
                    val uri = "${KeyValues.STADIUM_IMAGE_BASE_URL}${pitchImages[position].name}"
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

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<EditPhoto>() {
        override fun areItemsTheSame(oldItem: EditPhoto, newItem: EditPhoto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EditPhoto, newItem: EditPhoto): Boolean {
            return oldItem == newItem
        }
    }

    sealed class ViewHolder(binding: ViewBinding): RecyclerView.ViewHolder(binding.root){
        class AddImageViewHolder(val view: ItemAddImageBinding) : ViewHolder(view)

        class EditImageViewHolder(val view: ItemStadiumImageEditBinding) : ViewHolder(view)
    }
}
