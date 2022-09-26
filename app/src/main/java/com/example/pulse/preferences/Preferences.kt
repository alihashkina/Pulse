package com.example.pulse.preferences

import android.content.Context

class Preferences(context: Context){
    val PREFERANCE_NAME_SETTINGS = "settings"
    val PREFERANCE_VALUE = "PREFERANCE_VALUE"

    val preferences = context.getSharedPreferences(PREFERANCE_NAME_SETTINGS, Context.MODE_PRIVATE)

    fun getValue(): String? {
        return preferences.getString(PREFERANCE_VALUE, "60")
    }

    fun setValue(value: String) {
        val editor = preferences.edit()
        editor.putString(PREFERANCE_VALUE, value)
        editor.apply()
    }
}