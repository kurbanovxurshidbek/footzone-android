package com.footzone.footzone.helper

import android.widget.ImageView

interface OnClickEvent {
    fun setOnBookClickListener(stadiumId: String,isFavourite:Boolean)
    fun setOnNavigateClickListener(latitude: Double, longitude: Double)
    fun setOnBookMarkClickListener(stadiumId: String, ivBookmark: ImageView)
}