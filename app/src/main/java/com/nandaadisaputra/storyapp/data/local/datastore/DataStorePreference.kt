package com.nandaadisaputra.storyapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.nandaadisaputra.storyapp.data.local.constant.Const.CONSTANTS.DARK_MODE_KEY
import com.nandaadisaputra.storyapp.data.local.constant.Const.CONSTANTS.THEME_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class DataStorePreference(context: Context) {


    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = THEME_KEY)

    companion object {
        val darkModeKey = booleanPreferencesKey(DARK_MODE_KEY)
    }

    private val dataStore = context.dataStore

    suspend fun setTheme(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[darkModeKey] = isDarkMode
        }
    }

    fun getTheme(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val uiMode = preferences[darkModeKey] ?: false
                uiMode
            }
    }
}