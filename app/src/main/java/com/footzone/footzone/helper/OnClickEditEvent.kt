package com.footzone.footzone.helper

import android.widget.ImageView

interface OnClickEditEvent {
    fun setOnAddClickListener()
    fun setOnEditClickListener(position: Int, id: String)
    fun setOnDeleteClickListener(position: Int, id: String)
}