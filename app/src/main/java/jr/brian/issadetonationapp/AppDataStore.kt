package jr.brian.issadetonationapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore:
                DataStore<Preferences> by preferencesDataStore("app-data-store")
        val MINUTES = stringPreferencesKey("minutes")
        val SECONDS = stringPreferencesKey("seconds")
        val SOUND_PATH = stringPreferencesKey("sound")
    }

    val getMinutes: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[MINUTES]
    }

    suspend fun saveMinutes(value: String) {
        context.dataStore.edit { preferences ->
            preferences[MINUTES] = value
        }
    }

    val getSeconds: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[SECONDS]
    }

    suspend fun saveSeconds(value: String) {
        context.dataStore.edit { preferences ->
            preferences[SECONDS] = value
        }
    }

    val getSoundPath: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[SOUND_PATH]
    }

    suspend fun saveSoundPath(value: String) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_PATH] = value
        }
    }
}