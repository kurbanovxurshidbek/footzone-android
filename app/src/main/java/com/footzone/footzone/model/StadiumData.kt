package com.footzone.footzone.model

import com.footzone.footzone.model.holders.Comment
import com.footzone.footzone.model.holderstadium.IsOpen
import com.footzone.footzone.model.holderstadium.Photo

data class StadiumData(
    val address: String,
    val comments: ArrayList<Comment>,
    val hourlyPrice: Double,
    val isOpen: IsOpen,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val number: String,
    val photos: List<Photo>,
    val stadiumId: String
)