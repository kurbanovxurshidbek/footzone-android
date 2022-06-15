package com.footzone.footzone.model.sessionsday

import com.footzone.footzone.model.TimeManager

data class SessionsData(
    val sessionTimes: ArrayList<SessionTime>,
    val workingEndTime: String,
    val workingStartTime: String
)