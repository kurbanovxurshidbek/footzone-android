package com.footzone.footzone.model

data class StadiumDataToBottomSheetDialog(
    val stadiumId: String,
    val hourlyPrice: Long,
    val workingDays: List<WorkingDay>
)
