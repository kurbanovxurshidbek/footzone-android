package com.footzone.footzone.model.sessionsday

data class SessionsData(
    val sessionTimes: ArrayList<SessionTime>,
    val workingEndTime: String,
    val workingStartTime: String
)