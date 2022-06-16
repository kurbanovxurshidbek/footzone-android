package com.footzone.footzone.model

data class PlayedHistoryResponse(
    val message: String,
    val success: Boolean,
    val data: List<PlayedHistoryResponseData>
)

data class PlayedHistoryResponseData(
    val id: String,
    val status: String,
    val stadiumId: String,
    val startDate: String,
    val hourlyPrice: Long,
    val stadiumName: String,
    val startTime: String,
    val endTime: String
)

