package com.nuviotv.app.data.repository

import com.nuviotv.app.data.api.NetworkClient
import com.nuviotv.app.data.local.AddonPreferences
import com.nuviotv.app.data.model.AddonConfig
import com.nuviotv.app.data.model.AddonManifest
import com.nuviotv.app.data.model.Meta
import com.nuviotv.app.data.model.Stream
import com.nuviotv.app.data.model.Subtitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AddonRepository(private val prefs: AddonPreferences) {

    val installedAddons: Flow<List<AddonConfig>> = prefs.addons
    val favorites: Flow<List<String>> = prefs.favorites
    val watched: Flow<List<com.nuviotv.app.data.local.WatchedItem>> = prefs.watched

    suspend fun installAddon(transportUrl: String): Result<AddonConfig> =
        withContext(Dispatchers.IO) {
            try {
                val cleanUrl = transportUrl.trim().trimEnd('/')
                val api = NetworkClient.apiFor(cleanUrl)
                val manifest: AddonManifest = api.getManifest("$cleanUrl/manifest.json")
                val config = AddonConfig(
                    transportUrl = cleanUrl,
                    manifest = manifest
                )
                prefs.addAddon(config)
                Result.success(config)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun removeAddon(url: String) = prefs.removeAddon(url)

    suspend fun toggleAddon(url: String, enabled: Boolean) = prefs.toggleAddon(url, enabled)

    suspend fun toggleFavorite(metaId: String) = prefs.toggleFavorite(metaId)

    suspend fun markWatched(item: com.nuviotv.app.data.local.WatchedItem) =
        prefs.markWatched(item)

    /**
     * Fetch catalogs from all enabled addons that support the given type.
     */
    suspend fun loadCatalogs(type: String = "movie"): List<CatalogResult> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            val results = addons.map { addon ->
                async {
                    val catalogs = addon.manifest.catalogs.filter { it.type == type }
                    catalogs.map { catalog ->
                        async {
                            try {
                                val api = NetworkClient.apiFor(addon.transportUrl)
                                val response = api.getCatalog(
                                    baseUrl = addon.transportUrl,
                                    type = catalog.type,
                                    id = catalog.id,
                                    skip = 0
                                )
                                CatalogResult(
                                    addonName = addon.manifest.name,
                                    addonUrl = addon.transportUrl,
                                    catalogName = catalog.name,
                                    type = catalog.type,
                                    id = catalog.id,
                                    metas = response.metas
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                }.await()
            }.awaitAll().flatten().awaitAll().filterNotNull()
            results
        }

    suspend fun loadMeta(type: String, id: String): Meta? =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            for (addon in addons) {
                val supportsMeta = addon.manifest.resources.any { it.name == "meta" }
                if (!supportsMeta) continue
                try {
                    val api = NetworkClient.apiFor(addon.transportUrl)
                    val response = api.getMeta(addon.transportUrl, type, id)
                    return@withContext response.meta
                } catch (e: Exception) {
                    // try next
                }
            }
            null
        }

    suspend fun loadStreams(type: String, id: String): List<StreamResult> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            val results = addons.map { addon ->
                async {
                    val supportsStream = addon.manifest.resources.any { it.name == "stream" }
                    if (!supportsStream) return@async emptyList()
                    try {
                        val api = NetworkClient.apiFor(addon.transportUrl)
                        val response = api.getStreams(addon.transportUrl, type, id)
                        response.streams.map { stream ->
                            StreamResult(
                                addonName = addon.manifest.name,
                                stream = stream
                            )
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }.awaitAll().flatten()
            results
        }

    suspend fun loadSubtitles(type: String, id: String): List<SubtitleResult> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            val results = addons.map { addon ->
                async {
                    val supportsSubs = addon.manifest.resources.any { it.name == "subtitles" }
                    if (!supportsSubs) return@async emptyList()
                    try {
                        val api = NetworkClient.apiFor(addon.transportUrl)
                        val response = api.getSubtitles(addon.transportUrl, type, id)
                        response.subtitles.map { sub ->
                            SubtitleResult(
                                addonName = addon.manifest.name,
                                subtitle = sub
                            )
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }.awaitAll().flatten()
            results
        }

    suspend fun search(query: String, type: String = "movie"): List<Meta> =
        withContext(Dispatchers.IO) {
            val addons = installedAddons.first().filter { it.isEnabled }
            val results = addons.map { addon ->
                async {
                    val supportsSearch = addon.manifest.catalogs.any {
                        it.type == type && it.extra?.any { e -> e.name == "search" } == true
                    }
                    if (!supportsSearch) return@async emptyList()
                    val catalog = addon.manifest.catalogs.first {
                        it.type == type && it.extra?.any { e -> e.name == "search" } == true
                    }
                    try {
                        val api = NetworkClient.apiFor(addon.transportUrl)
                        val response = api.getCatalog(
                            baseUrl = addon.transportUrl,
                            type = type,
                            id = catalog.id,
                            search = query
                        )
                        response.metas
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }.awaitAll().flatten()
            results.distinctBy { it.id }
        }
}

data class CatalogResult(
    val addonName: String,
    val addonUrl: String,
    val catalogName: String,
    val type: String,
    val id: String,
    val metas: List<Meta>
)

data class StreamResult(
    val addonName: String,
    val stream: Stream
)

data class SubtitleResult(
    val addonName: String,
    val subtitle: Subtitle
)
