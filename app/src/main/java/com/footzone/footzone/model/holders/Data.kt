package com.footzone.footzone.model.holders

data class Data(
    val comments: List<Comment>,
    val hourlyPrice: Double,
    val isOpen: IsOpen,
    val name: String,
    val photos: List<Photo>,
    val stadiumId: String
)