package com.footzone.footzone.utils.commonfunction

import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.footzone.footzone.R
import com.footzone.footzone.model.Comment
import com.footzone.footzone.model.RateNumberPercentage
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

    fun rateNumbers(comments: ArrayList<Comment>): RateNumberPercentage {
        val sumOfAllRates = comments.sumOf { it.number * it.rate }
       return RateNumberPercentage(0, 0, 0, 0, 0).apply {
            comments.forEach {
                when (it.rate) {
                    1 -> this.one = it.number * it.rate * 100 / sumOfAllRates
                    2 -> this.two = it.number * it.rate * 100 / sumOfAllRates
                    3 -> this.three = it.number * it.rate * 100 / sumOfAllRates
                    4 -> this.four = it.number * it.rate * 100 / sumOfAllRates
                    5 -> this.five = it.number * it.rate * 100 / sumOfAllRates
                }
            }
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