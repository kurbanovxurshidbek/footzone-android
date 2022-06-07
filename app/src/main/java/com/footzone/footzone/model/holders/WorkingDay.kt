package com.footzone.footzone.model.holders

data class WorkingDay(
    val createdAt: String,
    val createdBy: Any,
    val dayName: String,
    val endTime: EndTime,
    val id: String,
    val startTime: StartTime,
    val updatedAt: String,
    val updatedBy: Any
)