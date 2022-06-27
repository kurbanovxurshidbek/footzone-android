package com.footzone.footzone.model

data class StadiumBookSentResponse(
    val message: String,
    val success: Boolean,
    val data: List<StadiumBookSentResponseData>
)

data class StadiumBookSentResponseData(
    val id: String,
    val stadiumName: String,
    val startDate: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val hourlyPrice: Long
)
