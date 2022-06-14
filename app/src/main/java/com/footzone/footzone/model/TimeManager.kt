package com.footzone.footzone.model

import java.time.LocalTime

data class TimeManager(
    val startTime: LocalTime? = null,
    var finishTime: LocalTime? = null,
    var status: String? = null,
    var isSelected: Boolean? = false,
    var between: Boolean? = false
)