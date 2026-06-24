package ru.fefu.task3.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserSettings @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val ACTIVE_USER_ID = longPreferencesKey("active_user_id")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val activeUserId: Flow<Long?> = context.dataStore.data
        .map { preferences -> preferences[ACTIVE_USER_ID] }

    val isDarkTheme: Flow<Boolean?> = context.dataStore.data
        .map { preferences -> preferences[DARK_THEME] }

    suspend fun setActiveUserId(userId: Long?) {
        context.dataStore.edit { preferences ->
            if (userId != null) {
                preferences[ACTIVE_USER_ID] = userId
            } else {
                preferences.remove(ACTIVE_USER_ID)
            }
        }
    }

    suspend fun setDarkTheme(enabled: Boolean?) {
        context.dataStore.edit { preferences ->
            if (enabled != null) {
                preferences[DARK_THEME] = enabled
            } else {
                preferences.remove(DARK_THEME)
            }
        }
    }
}
