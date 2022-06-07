package com.footzone.footzone.model

data class StadiumData(
    val stadiumID: String,
    val photos: List<Photo>,
    val name: String,
    val hourlyPrice: Long,
    val workingDays: List<WorkingDay>
)

data class Photo(
    val id: String,
    val name: String
)

data class WorkingDay(
    val id: String,
    val dayName: String,
    val startTime: TimeWorking,
    val endTimeWorking: TimeWorking
)

data class TimeWorking(
    val id: String,
    val time: String
)

