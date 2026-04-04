package com.example.sokolovtodolist.network

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString("bearer_token", null)
        set(value) {
            prefs.edit().putString("bearer_token", value).apply()
        }

    fun clear() {
        prefs.edit().remove("bearer_token").apply()
    }
}