package com.footzone.footzone.model

import java.time.LocalDate
import java.time.LocalTime

class StadiumBookSentResponse(
    val message: String,
    val success: Boolean,
    val data: ArrayList<StadiumBookSentResponseData>
)

data class StadiumBookSentResponseData(
    val sessionId: String,
    val stadiumName: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val hourlyPrice: String,
    val status: String
)
