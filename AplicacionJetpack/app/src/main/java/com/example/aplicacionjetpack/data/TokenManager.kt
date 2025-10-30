package com.example.aplicacionjetpack.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PREFS_NAME = "auth_prefs"
    private val PREFS_KEY_TOKEN = "token"
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(PREFS_KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(PREFS_KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(PREFS_KEY_TOKEN).apply()
    }
}
