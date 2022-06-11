package com.footzone.footzone.model.holderpitchs

data class Data(
    val comments: List<Comment>,
    val hourlyPrice: Double,
    val isActive: Boolean,
    val isOpen: IsOpen,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val photos: List<String>,
    val stadiumId: String
)