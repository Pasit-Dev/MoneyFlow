package com.example.moneyflow_jetpackcompose.ui.theme

import android.content.Context
import android.content.res.Resources.Theme
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreference(private val context: Context) {
    companion object {
        val THEME_KEY = intPreferencesKey("theme_mode")
    }

    val themeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // Default to system theme
            preferences[THEME_KEY] ?: 0
        }

    suspend fun saveTheme(themeMode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode
        }
    }


}

enum class ThemeMode(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromInt(value: Int) = ThemeMode.values().first { it.value == value}
    }
}