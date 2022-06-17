package com.footzone.footzone.helper

import android.widget.ImageView

interface OnClickEditEvent {
    fun setOnAddClickListener()
    fun setOnDeleteClickListener(position: Int, id: String? = null)
}