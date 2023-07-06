package com.nandaadisaputra.storyapp.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences

class LoginPreference(context: Context) {
    var context: Context? = context
    private val tokenExtra = "TOKEN"
    private var privateMode = 0
    private var preferenceName = "story app"
    val isLogin = "LOGIN"
    val emailUser: String = "email"
    val passwordUser: String = "password"
    val tokenUser: String = "token"
    val usernameUser = "username"
    val idUser: String = "id"

    private var pref: SharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
        //EncryptionSharedPreference adalah nama file shared preference.
        EncryptedSharedPreferences
            .create(
                context,
                "SharedPreference",
                masterKey,
                //algoritma AES GCM 256
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    } else {
        SecurePreferences(context)
    }
    private var editor: SharedPreferences.Editor = pref.edit()
    fun put(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return pref.getString(key, null)
    }

    fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun setToken(token: String?) {
        pref.edit { putString(tokenExtra, token) }
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }
    fun savePrefBoolean(name: String, value: Boolean) {
        editor.putBoolean(name, value)
        editor.commit()
    }
    fun setIsLoggedIn(isLoggedIn: Boolean) {
        pref.edit { putBoolean(isLogin, isLoggedIn) }
    }
    fun getIsLogin(): Boolean {
        return pref.getBoolean(isLogin, false)
    }
    fun logout() {
        editor.apply {
            clear()
            commit()
            apply()
        }
    }
}