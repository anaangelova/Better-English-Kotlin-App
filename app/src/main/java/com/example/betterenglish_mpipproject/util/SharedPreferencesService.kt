package com.example.betterenglish_mpipproject.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(var context: Context) {
    var preferences: SharedPreferences
    var editor: SharedPreferences.Editor
    private var PRIVATE_MODE: Int = 0

    companion object {
        val PREF_NAME = "BetterEnglishSP"
    }

    init {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences.edit()
    }


    fun saveData(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String?): String? {
        return preferences.getString(key, "")
    }
}