package com.example.cahier.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cahier.core.navigation.HomePagePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val HOME_PAGE_PREFERENCE = stringPreferencesKey("home_page_preference")
    }

    val homePagePreference: Flow<HomePagePreference> = context.dataStore.data
        .map { preferences ->
            val value = preferences[PreferencesKeys.HOME_PAGE_PREFERENCE] ?: HomePagePreference.HOME.name
            try {
                HomePagePreference.valueOf(value)
            } catch (e: IllegalArgumentException) {
                HomePagePreference.HOME
            }
        }

    suspend fun saveHomePagePreference(preference: HomePagePreference) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HOME_PAGE_PREFERENCE] = preference.name
        }
    }
}