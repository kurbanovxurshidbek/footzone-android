package com.footzone.footzone.utils.commonfunction

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
}