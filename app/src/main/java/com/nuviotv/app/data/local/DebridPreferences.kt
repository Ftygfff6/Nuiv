package com.nuviotv.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.debridStore by preferencesDataStore(name = "debrid_prefs")

class DebridPreferences(private val context: Context) {
    companion object {
        private val KEY_RD = stringPreferencesKey("real_debrid_key")
        private val KEY_TORBOX = stringPreferencesKey("torbox_key")
        private val KEY_PREMIUMIZE = stringPreferencesKey("premiumize_key")
        private val KEY_METADATA_SOURCE = stringPreferencesKey("metadata_source")
        private val KEY_TMDB_KEY = stringPreferencesKey("tmdb_key")
    }

    val realDebridKey: Flow<String> = context.debridStore.data.map { it[KEY_RD] ?: "" }
    val torboxKey: Flow<String> = context.debridStore.data.map { it[KEY_TORBOX] ?: "" }
    val premiumizeKey: Flow<String> = context.debridStore.data.map { it[KEY_PREMIUMIZE] ?: "" }
    val metadataSource: Flow<String> = context.debridStore.data.map { it[KEY_METADATA_SOURCE] ?: "cinemeta" }
    val tmdbKey: Flow<String> = context.debridStore.data.map { it[KEY_TMDB_KEY] ?: "" }

    suspend fun setRealDebridKey(key: String) = context.debridStore.edit { it[KEY_RD] = key }
    suspend fun setTorboxKey(key: String) = context.debridStore.edit { it[KEY_TORBOX] = key }
    suspend fun setPremiumizeKey(key: String) = context.debridStore.edit { it[KEY_PREMIUMIZE] = key }
    suspend fun setMetadataSource(source: String) = context.debridStore.edit { it[KEY_METADATA_SOURCE] = source }
    suspend fun setTmdbKey(key: String) = context.debridStore.edit { it[KEY_TMDB_KEY] = key }
}
