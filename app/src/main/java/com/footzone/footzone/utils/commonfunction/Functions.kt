package com.footzone.footzone.utils.commonfunction

import android.widget.ImageView
import com.footzone.footzone.R
import com.footzone.footzone.model.Comment
import java.lang.Exception

object Functions {
    fun resRating(comments: ArrayList<Comment>): Float {
        return try {
            (comments.sumOf { it.number * it.rate } / comments.sumOf { it.number }).toFloat()
        } catch (e: Exception) {
            2.5f
        }
    }

    fun ImageView.setFavouriteBackground() {
        this.setBackgroundResource(R.drawable.imageview_circle_fill_blue)
        this.setImageResource(R.drawable.ic_bookmark_white)
    }

    fun ImageView.setUnFavouriteBackground() {
        this.setBackgroundResource(R.drawable.imageview_circle)
        this.setImageResource(R.drawable.ic_bookmark)
    }
}