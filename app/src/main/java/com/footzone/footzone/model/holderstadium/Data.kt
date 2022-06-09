package com.footzone.footzone.model.holderstadium

data class Data(
    val address: String,
    val comments: List<Comment>,
    val hourlyPrice: Double,
    val isOpen: IsOpen,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val number: String,
    val photos: List<Photo>,
    val stadiumId: String
)