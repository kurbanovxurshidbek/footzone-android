package com.footzone.footzone.model

import java.io.Serializable

data class Pitch(
    val id: String,
    var images: ArrayList<String>,
    var name: String?,
    var rating: Float,
    var ratingNums: Int,
    var isOpen: Boolean,
    var time: Time,
    var price: Int
) : Serializable