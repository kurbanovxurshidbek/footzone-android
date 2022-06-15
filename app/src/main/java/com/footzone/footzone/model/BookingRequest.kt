package com.footzone.footzone.model

data class BookingRequest(
    val stadiumId: String,
    val startDate: String,
    val startTime: String,
    val endTime: String
)
