package com.footzone.footzone.utils

sealed class UiStateList<out T> {
    data class SUCCESS<out T>(val data: List<T>,var type:String = ""):UiStateList<T>()
    data class ERROR(val message:String,var fromServer:Boolean = false):UiStateList<Nothing>()
    object LOADING : UiStateList<Nothing>()
    object EMPTY : UiStateList<Nothing>()
}