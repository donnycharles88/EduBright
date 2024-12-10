package com.capstone.education.edubright.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference constructor(private val context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val THEME_KEY = booleanPreferencesKey("theme_preference")

    fun getSession(): Flow<UserModel> {
        return context.dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }
    fun getThemePreference(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false // Default ke light mode
        }
    }

    suspend fun setThemePreference(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkMode
        }
    }


    suspend fun saveSession(user: UserModel) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[DATA_KEY] = ""
            preferences[IS_LOGIN_KEY] = false
        }
    }

    companion object {
        const val DATASOURCE_NAME = "settings"
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val DATA_KEY = stringPreferencesKey("data")

        fun getInstance(context: Context): UserPreference {
            var instance: UserPreference? = null
            if (instance == null) {
                instance = UserPreference(context)
            }
            return instance
        }
    }
}
