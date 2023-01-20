package com.nandaadisaputra.storyapp.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class LoginPreference(context: Context) {
    var context: Context? = context
    var preference: SharedPreferences
    private var editor: SharedPreferences.Editor
    private val tokenExtra = "TOKEN"
    private var privateMode = 0
    private var preferenceName = "story app"
    val isLogin = "LOGIN"
    val emailUser: String = "email"
    val passwordUser: String = "password"
    val tokenUser: String = "token"
    val usernameUser = "username"
    val idUser: String = "id"

    init {
        preference = context.getSharedPreferences(preferenceName, privateMode)
        editor = preference.edit()
    }

    fun put(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return preference.getString(key, null)
    }

    fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun setToken(token: String?) {
        preference.edit { putString(tokenExtra, token) }
    }

    fun getBoolean(key: String): Boolean {
        return preference.getBoolean(key, false)
    }
    fun savePrefBoolean(name: String, value: Boolean) {
        editor.putBoolean(name, value)
        editor.commit()
    }
    fun setIsLoggedIn(isLoggedIn: Boolean) {
        preference.edit { putBoolean(isLogin, isLoggedIn) }
    }
    fun getIsLogin(): Boolean {
        return preference.getBoolean(isLogin, false)
    }
    fun logout() {
        editor.apply {
            clear()
            commit()
            apply()
        }
    }
}