package com.nuviotv.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nuviotv.app.data.model.AddonConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "niov_prefs")

class AddonPreferences(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    companion object {
        private val KEY_ADDONS = stringPreferencesKey("addons_json")
        private val KEY_FAVORITES = stringPreferencesKey("favorites_json")
        private val KEY_WATCHED = stringPreferencesKey("watched_json")
    }

    // -------- Addons --------
    val addons: Flow<List<AddonConfig>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_ADDONS] ?: return@map emptyList()
        try {
            json.decodeFromString<List<AddonConfig>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveAddons(addons: List<AddonConfig>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ADDONS] = json.encodeToString<List<AddonConfig>>(addons)
        }
    }

    suspend fun addAddon(config: AddonConfig) {
        val current = addons.first()
        val updated = current.filter { it.transportUrl != config.transportUrl } + config
        saveAddons(updated.mapIndexed { index, item -> item.copy(order = index) })
    }

    suspend fun removeAddon(url: String) {
        val current = addons.first()
        saveAddons(current.filter { it.transportUrl != url })
    }

    suspend fun toggleAddon(url: String, enabled: Boolean) {
        val current = addons.first()
        saveAddons(current.map {
            if (it.transportUrl == url) it.copy(isEnabled = enabled) else it
        })
    }

    // -------- Favorites --------
    val favorites: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_FAVORITES] ?: return@map emptyList()
        try {
            json.decodeFromString<List<String>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleFavorite(metaId: String) {
        val current = favorites.first()
        val updated = if (current.contains(metaId)) current - metaId else current + metaId
        context.dataStore.edit { prefs ->
            prefs[KEY_FAVORITES] = json.encodeToString<List<String>>(updated)
        }
    }

    // -------- Watched --------
    val watched: Flow<List<WatchedItem>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_WATCHED] ?: return@map emptyList()
        try {
            json.decodeFromString<List<WatchedItem>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun markWatched(item: WatchedItem) {
        val current = watched.first()
        val updated = current.filter { it.metaId != item.metaId } + item
        val sorted = updated.sortedByDescending { it.lastWatched }.take(20)
        context.dataStore.edit { prefs ->
            prefs[KEY_WATCHED] = json.encodeToString<List<WatchedItem>>(sorted)
        }
    }
}

@kotlinx.serialization.Serializable
data class WatchedItem(
    val metaId: String,
    val title: String,
    val poster: String? = null,
    val type: String,
    val progressMs: Long,
    val durationMs: Long,
    val lastWatched: Long = System.currentTimeMillis()
)
