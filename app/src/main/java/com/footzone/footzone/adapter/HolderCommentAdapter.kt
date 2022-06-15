package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemCommentBinding
import com.footzone.footzone.model.FullComment
import com.footzone.footzone.utils.KeyValues.USER_IMAGE_BASE_URL

class HolderCommentAdapter(var items: ArrayList<FullComment>) :
    RecyclerView.Adapter<HolderCommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = items[position]
        holder.view.apply {
            commentOwnerName.text = item.userFullName
            ratingBarComment.rating = item.rate
            ratingBarComment.setIsIndicator(true)
            bodyComment.text = item.text
            dateComment.text = item.createdAt

            val uri = "${USER_IMAGE_BASE_URL}${item.userAttachmentName}"
            Glide.with(ivUserPhoto.context)
                .load(uri)
                .placeholder(R.drawable.ic_avatar)
                .into(ivUserPhoto)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class CommentViewHolder(val view: ItemCommentBinding) : RecyclerView.ViewHolder(view.root)
}