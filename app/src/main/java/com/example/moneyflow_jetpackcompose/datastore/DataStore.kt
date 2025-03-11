package com.example.moneyflow_jetpackcompose.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.moneyflow_jetpackcompose.ui.theme.dataStore
import kotlinx.coroutines.flow.first

//private val Context.dataStore by preferencesDataStore("app_prefs")
//
//suspend fun saveUserId(context:Context, uuid: String) {
//    val dataStoreKey = stringPreferencesKey("user_id")
//    context.dataStore.edit { preference ->
//        preference[dataStoreKey] = uuid
//    }
//}
//
//suspend fun getUserId(context: Context): String? {
//    val dataStoreKey = stringPreferencesKey("user_id")
//    val preferences = context.dataStore.data.first()
//    return preferences[dataStoreKey]
//}
//
//suspend fun clearUserId(context: Context) {
//    val dataStoreKey = stringPreferencesKey("user_id")
//    context.dataStore.edit { preference ->
//        preference.remove(dataStoreKey)
//    }
//}

object DataStoreManager {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}