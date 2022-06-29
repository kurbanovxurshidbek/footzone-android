package com.footzone.footzone.model

data class SessionNotificationResponse(
    val stadiumName: String,
    val stadiumHolder: Boolean,
    val startTime: String,
    val sessionId: String,
    val endTime: String,
    val startDate: String
)