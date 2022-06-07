package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemCommentBinding
import com.footzone.footzone.model.holders.Comment

class CommentAdapter(var items: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {



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
//            commentOwnerName.text = item.name
//            ratingBarComment.rating = item.rate
//            ratingBarComment.setIsIndicator(true)
//            bodyComment.text = item.body
//            dateComment.text = item.date
        }
    }

    override fun getItemCount(): Int = items.size

    inner class CommentViewHolder(val view: ItemCommentBinding) : RecyclerView.ViewHolder(view.root)
}