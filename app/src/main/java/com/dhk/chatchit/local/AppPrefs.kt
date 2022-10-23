package com.dhk.chatchit.local

import android.content.SharedPreferences
import androidx.core.content.edit

class AppPrefs(private val sharedPreferences: SharedPreferences)  {

    fun putString(key:String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    fun getString(key:String): String? =
        sharedPreferences.getString(key, null)

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit{putInt(key, value)}
    }

    fun getInt(key:String): Int =
        sharedPreferences.getInt(key, 0)

    fun clearData() {
        sharedPreferences.edit{clear()}
    }
}