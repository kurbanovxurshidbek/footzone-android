package com.footzone.footzone.helper

interface OnClickEditEvent {
    fun setOnAddClickListener()
    fun setOnDeleteClickListener(position: Int, id: String? = null)
}