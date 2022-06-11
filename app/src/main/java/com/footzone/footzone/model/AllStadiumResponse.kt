package com.footzone.footzone.model

data class AllStadiumResponse(
    val message: String,
    val success: Boolean,
    val data: ArrayList<StadiumLocationName>
)

data class StadiumLocationName(
    val longitude: Double,
    val latitude: Double,
    val stadiumId: String,
    val isActive: Boolean,
    val name: String
)