package com.footzone.footzone.model

data class StadiumDataToBottomSheetDialog(
    val stadiumId: String,
    val hourlyPrice: Int,
    val workingDays: List<WorkingDay>
)
