package com.footzone.footzone.utils.commonfunction

import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.footzone.footzone.R
import com.footzone.footzone.model.Comment
import java.lang.Exception
import java.time.Duration
import java.time.LocalTime

object Functions {
    fun resRating(comments: ArrayList<Comment>): Float {
        return try {
            (comments.sumOf { it.number * it.rate } / comments.sumOf { it.number }).toFloat()
        } catch (e: Exception) {
            2.5f
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateInHours(startTime: LocalTime, endTime: LocalTime): Double {
        return Duration.between(startTime, endTime).toMillis() / 60.0
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