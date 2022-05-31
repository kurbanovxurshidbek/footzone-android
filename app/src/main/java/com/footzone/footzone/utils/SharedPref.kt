package com.footzone.footzone.utils

import android.content.Context

class SharedPref(private val context: Context) {
    val sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    fun saveLogIn(key: String, data: Boolean) {
        sharedPref.edit().putBoolean(key, data).apply()
    }
    fun getLogIn(key: String, data: Boolean):Boolean {
        return sharedPref.getBoolean(key,false)
    }

}