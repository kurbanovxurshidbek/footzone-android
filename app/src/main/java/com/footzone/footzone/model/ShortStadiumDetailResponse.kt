package com.footzone.footzone.model

data class ShortStadiumDetailResponse(
    val message: String,
    val success: Boolean,
    val data: ArrayList<ShortStadiumDetail>
)

data class ShortStadiumDetail(
    val stadiumId: String,
    val hourlyPrice: Int,
    val longitude: Double,
    val latitude: Double,
    val photos: ArrayList<String>,
    val isActive: Boolean,
    val isOpen: IsOpen,
    val comments: ArrayList<Comment>,
    val name: String
)

data class Comment(
    val number: Int,
    val rate: Int
)
