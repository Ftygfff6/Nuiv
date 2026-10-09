package com.nuviotv.app.data.repository

import android.util.Log
import com.nuviotv.app.data.api.NetworkClient
import com.nuviotv.app.data.local.AddonPreferences
import com.nuviotv.app.data.local.WatchedItem
import com.nuviotv.app.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AddonRepository(private val prefs: AddonPreferences) {
    private val TAG = "AddonRepo"

    val installedAddons: Flow<List<AddonConfig>> = prefs.addons
    val favorites: Flow<List<String>> = prefs.favorites
    val watched: Flow<List<WatchedItem>> = prefs.watched

    suspend fun installAddon(transportUrl: String): Result<AddonConfig> =
        withContext(Dispatchers.IO) {
            try {
                val cleanUrl = transportUrl.trim().trimEnd('/')
                val manifest = NetworkClient.api.getManifest("$cleanUrl/manifest.json")
                val config = AddonConfig(transportUrl = cleanUrl, manifest = manifest)
                prefs.addAddon(config)
                Result.success(config)
            } catch (e: Exception) {
                Result.failure(Exception("${e.javaClass.simpleName}: ${e.message}"))
            }
        }

    suspend fun removeAddon(url: String) = prefs.removeAddon(url)
    suspend fun toggleAddon(url: String, enabled: Boolean) = prefs.toggleAddon(url, enabled)
    suspend fun toggleFavorite(metaId: String) = prefs.toggleFavorite(metaId)
    suspend fun markWatched(item: WatchedItem) = prefs.markWatched(item)

    private fun url(base: String, path: String) = "${base.trimEnd('/')}/${path.trimStart('/')}"

    suspend fun loadCatalogs(type: String): List<CatalogResult> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            coroutineScope {
                val tasks = addons.flatMap { addon ->
                    addon.manifest.catalogs.filter { it.type == type }.map { catalog ->
                        async {
                            try {
                                val u = url(addon.transportUrl, "catalog/${catalog.type}/${catalog.id}.json")
                                val response = NetworkClient.api.getCatalog(u, skip = 0)
                                CatalogResult(addon.manifest.name, addon.transportUrl, catalog.name, catalog.type, catalog.id, response.metas)
                            } catch (e: Exception) { null }
                        }
                    }
                }
                tasks.awaitAll().filterNotNull()
            }
        }

    suspend fun loadMeta(type: String, id: String): Meta? =
        withContext(Dispatchers.IO) {
            for (addon in installedAddons.first().filter { it.isEnabled }) {
                if (!addon.manifest.resources.any { it.name == "meta" }) continue
                try {
                    return@withContext NetworkClient.api.getMeta(url(addon.transportUrl, "meta/$type/$id.json")).meta
                } catch (e: Exception) { }
            }
            null
        }

    suspend fun loadStreams(type: String, id: String): List<StreamResult> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            coroutineScope {
                val tasks = addons.map { addon ->
                    async {
                        if (!addon.manifest.resources.any { it.name == "stream" }) return@async emptyList()
                        try {
                            val u = url(addon.transportUrl, "stream/$type/$id.json")
                            NetworkClient.api.getStreams(u).streams.map { StreamResult(addon.manifest.name, it) }
                        } catch (e: Exception) { emptyList() }
                    }
                }
                tasks.awaitAll().flatten()
            }
        }

    suspend fun loadSubtitles(type: String, id: String): List<SubtitleResult> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            coroutineScope {
                val tasks = addons.map { addon ->
                    async {
                        if (!addon.manifest.resources.any { it.name == "subtitles" }) return@async emptyList()
                        try {
                            val u = url(addon.transportUrl, "subtitles/$type/$id.json")
                            NetworkClient.api.getSubtitles(u).subtitles.map { SubtitleResult(addon.manifest.name, it) }
                        } catch (e: Exception) { emptyList() }
                    }
                }
                tasks.awaitAll().flatten()
            }
        }

    suspend fun search(query: String, type: String = "movie"): List<Meta> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            coroutineScope {
                val tasks = addons.map { addon ->
                    async {
                        val cat = addon.manifest.catalogs.firstOrNull {
                            it.type == type && it.extra?.any { e -> e.name == "search" } == true
                        } ?: return@async emptyList()
                        try {
                            val u = url(addon.transportUrl, "catalog/$type/${cat.id}.json")
                            NetworkClient.api.getCatalog(u, skip = 0, search = query).metas
                        } catch (e: Exception) { emptyList() }
                    }
                }
                tasks.awaitAll().flatten().distinctBy { it.id }
            }
        }
}

data class CatalogResult(
    val addonName: String, val addonUrl: String, val catalogName: String,
    val type: String, val id: String, val metas: List<Meta>
)
data class StreamResult(val addonName: String, val stream: Stream)
data class SubtitleResult(val addonName: String, val subtitle: Subtitle)
