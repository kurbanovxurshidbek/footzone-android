package com.footzone.footzone.model

data class PlayingSoonHistoryResponse(
    val message: String,
    val success: Boolean,
    val data: List<PlayingSoonHistoryResponseData>
)

data class PlayingSoonHistoryResponseData(
    val id: String,
    val status: String,
    val stadiumId: String,
    val startDate: String,
    val hourlyPrice: Long,
    val stadiumName: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: String,
    val endTime: String
)

