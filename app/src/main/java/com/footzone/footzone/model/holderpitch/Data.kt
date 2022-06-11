package com.footzone.footzone.model.holderpitch

data class Data(
    val address: String,
    val hourlyPrice: Double,
    val isOpen: IsOpen,
    val latitude: Double,
    val longitude: Double,
    val number: String,
    val photos: List<Photo>,
    val stadiumId: String,
    val stadiumName: String
)