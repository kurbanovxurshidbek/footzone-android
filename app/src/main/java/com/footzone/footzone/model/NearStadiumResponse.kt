package com.footzone.footzone.model

data class NearStadiumResponse(
    val message: String,
    val success: Boolean,
    val data: ArrayList<StadiumData>
)