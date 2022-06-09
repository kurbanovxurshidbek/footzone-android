package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemCommentBinding
import com.footzone.footzone.model.holderstadium.Comment

class HolderCommentAdapter(var items: ArrayList<Comment>) :
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
            ratingBarComment.rating = item.rate.toFloat()
            ratingBarComment.setIsIndicator(true)
            bodyComment.text = item.text
            dateComment.text = item.createdAt

            val uri = "https://footzone-server.herokuapp.com/images/user/${item.userAttachmentName}"
            Glide.with(ivUserPhoto.context)
                .load(uri)
                .placeholder(R.drawable.ic_avatar)
                .into(ivUserPhoto)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class CommentViewHolder(val view: ItemCommentBinding) : RecyclerView.ViewHolder(view.root)
}