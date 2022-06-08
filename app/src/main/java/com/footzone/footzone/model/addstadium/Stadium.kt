package com.footzone.footzone.model.addstadium

data class Stadium(
    val address: String,
    val number: String,
    val hourlyPrice: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val workingDays: List<WorkingDay>
)