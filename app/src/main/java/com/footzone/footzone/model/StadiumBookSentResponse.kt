package com.footzone.footzone.model

import java.time.LocalDate
import java.time.LocalTime

data class StadiumBookSentResponse(
    val message: String,
    val success: Boolean,
    val data: List<StadiumBookSentResponseData>
)

data class StadiumBookSentResponseData(
    val sessionId: String,
    val stadiumName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val hourlyPrice: Long
)
