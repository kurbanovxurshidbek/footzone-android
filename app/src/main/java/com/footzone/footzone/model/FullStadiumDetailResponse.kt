package com.footzone.footzone.model


data class FullStadiumDetailResponse(
    val message: String,
    val success: Boolean,
    val data: StadiumData
)

data class StadiumData(
    val stadiumId: String,
    val hourlyPrice: Long,
    val longitude: Double,
    val latitude: Double,
    val photos: ArrayList<StadiumPhoto>,
    val isOpen: IsOpen,
    val stadiumName: String,
    val address: String,
    val number: String,
    val workingDays: List<WorkingDay>
)

data class StadiumPhoto(
    val id: String,
    val name: String
)

data class IsOpen(
    val time: String,
    val open: Boolean
)
