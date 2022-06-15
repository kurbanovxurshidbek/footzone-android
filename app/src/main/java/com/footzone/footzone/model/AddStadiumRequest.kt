package com.footzone.footzone.model

data class AddStadiumRequest(
    val address: String,
    val number: String,
    val hourlyPrice: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val userId: String,
    val workingDays: List<WorkingDay>
)

data class WorkingDay(
    val dayName: String,
    val endTime: String,
    val startTime: String
)