package com.footzone.footzone.helper

interface OnClickEvent {
    fun setOnBookClickListener(stadiumId: String)
    fun setOnNavigateClickListener(latitude: Double, longitude: Double)
    fun setOnBookMarkClickListener(stadiumId: String, stadiumName: String)
}