package com.vs.vibeplayer.main.data.favourite

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.vs.vibeplayer.main.domain.favourite.FavouritePrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class FavouritePrefsImpl(
    private val dataStore: DataStore<Preferences>
) : FavouritePrefs {
    private object Keys{
        val FAVORITE_TRACK_IDS = stringSetPreferencesKey("favorite_track_ids")

    }




    override suspend fun toggleFavourite(trackId: Long) {
        dataStore.edit { preferences ->
            val current = preferences[Keys.FAVORITE_TRACK_IDS] ?: emptySet()
            val trackIdString = trackId.toString()

            preferences[Keys.FAVORITE_TRACK_IDS] = if (current.contains(trackIdString)) {
                current - trackIdString
            } else {
                current + trackIdString
            }
        }
    }

    override suspend fun getfavouriteList(): Flow<Set<Long>> {
        return dataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { prefs->
                prefs[Keys.FAVORITE_TRACK_IDS]?.mapNotNull{ it.toLongOrNull() }?.toSet() ?: emptySet()
            }
    }

}