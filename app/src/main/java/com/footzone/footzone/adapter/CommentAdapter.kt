package com.footzone.footzone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.databinding.ItemCommentBinding
import com.footzone.footzone.model.AllComment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.DEFAULT
import com.footzone.footzone.utils.commonfunction.Functions.loadImageUrl

class CommentAdapter(var items: List<AllComment>, val context: Context) :
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
            commentOwnerName.text = item.userFullName
            ratingBarComment.rating = item.rate.toFloat()
            ratingBarComment.setIsIndicator(true)
            bodyComment.text = item.text
            dateComment.text = item.createdAt.subSequence(0, 10)

            if (!item.userAttachmentName.startsWith(DEFAULT)) {
                val uri = "${KeyValues.USER_IMAGE_BASE_URL}${item.userAttachmentName}"
                ivUserPhoto.loadImageUrl(uri)
            }

        }
    }

    override fun getItemCount(): Int = items.size

    inner class CommentViewHolder(val view: ItemCommentBinding) : RecyclerView.ViewHolder(view.root)
}