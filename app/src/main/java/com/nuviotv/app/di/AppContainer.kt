package com.nuviotv.app.di

import android.content.Context
import android.util.Log
import com.nuviotv.app.data.api.NetworkClient
import com.nuviotv.app.data.debrid.DebridKeyBuilder
import com.nuviotv.app.data.local.AddonPreferences
import com.nuviotv.app.data.local.DebridPreferences
import com.nuviotv.app.data.model.AddonConfig
import com.nuviotv.app.data.repository.AddonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object AppContainer {
    private const val TAG = "AppContainer"
    private var initialized = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var addonPreferences: AddonPreferences
        private set
    lateinit var debridPreferences: DebridPreferences
        private set
    lateinit var addonRepository: AddonRepository
        private set

    // حالة آخر محاولة تثبيت
    var lastInstallError: String? = null
        private set
    var lastInstallSuccess: String? = null
        private set

    fun init(context: Context) {
        if (initialized) return
        addonPreferences = AddonPreferences(context.applicationContext)
        debridPreferences = DebridPreferences(context.applicationContext)
        addonRepository = AddonRepository(addonPreferences)
        initialized = true

        scope.launch {
            try {
                val existing = addonPreferences.addons.first()
                val urls = existing.map { it.transportUrl }.toSet()
                if (DebridKeyBuilder.CINEMETA_URL !in urls) {
                    Log.d(TAG, "Installing Cinemeta...")
                    installCinemeta()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Init failed", e)
            }
        }
    }

    private suspend fun installCinemeta() {
        val cleanUrl = DebridKeyBuilder.CINEMETA_URL
        val manifestUrl = "$cleanUrl/manifest.json"
        try {
            Log.d(TAG, "Fetching: $manifestUrl")
            val manifest = NetworkClient.api.getManifest(manifestUrl)
            Log.d(TAG, "Manifest OK: ${manifest.name}, catalogs=${manifest.catalogs.size}")

            val config = AddonConfig(transportUrl = cleanUrl, manifest = manifest)
            addonPreferences.addAddon(config)
            lastInstallSuccess = "✅ Cinemeta مثبتة: ${manifest.name}"
            lastInstallError = null
            Log.d(TAG, lastInstallSuccess!!)
        } catch (e: Exception) {
            val err = "${e.javaClass.simpleName}: ${e.message ?: "no message"}"
            lastInstallError = "❌ $err"
            Log.e(TAG, "Cinemeta install FAILED: $err", e)
        }
    }

    fun refreshTorrentio() {
        scope.launch {
            try {
                val rd = debridPreferences.realDebridKey.first()
                val tb = debridPreferences.torboxKey.first()
                val pm = debridPreferences.premiumizeKey.first()
                val url = DebridKeyBuilder.buildTorrentioUrl(rd, tb, pm)
                val existing = addonPreferences.addons.first()
                existing.forEach {
                    if (it.transportUrl.contains("torrentio")) addonRepository.removeAddon(it.transportUrl)
                }
                addonRepository.installAddon(url)
            } catch (e: Exception) {
                Log.e(TAG, "Torrentio refresh failed", e)
            }
        }
    }
}
