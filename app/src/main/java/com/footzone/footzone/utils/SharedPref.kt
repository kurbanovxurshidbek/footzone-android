package com.footzone.footzone.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(@ApplicationContext val context: Context) {
    val sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

    fun saveLogIn(key: String, data: Boolean) {
        sharedPref.edit().putBoolean(key, data).apply()
    }

    fun getLogIn(key: String, data: Boolean): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun saveUserId(key: String, data: String) {
        sharedPref.edit().putString(key, data).apply()
    }

    fun getUserID(key: String, data: String): String {
        return sharedPref.getString(key, data)!!
    }

    fun saveUserToken(key: String, data: String) {
        sharedPref.edit().putString(key, data).apply()
    }

    fun getUserToken(key: String, data: String): String {
        return sharedPref.getString(key, data)!!
    }
}