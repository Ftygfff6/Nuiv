package com.nuviotv.app.di

import android.content.Context
import android.util.Log
import com.nuviotv.app.data.debrid.DebridKeyBuilder
import com.nuviotv.app.data.local.AddonPreferences
import com.nuviotv.app.data.local.DebridPreferences
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

    fun init(context: Context) {
        if (initialized) return
        addonPreferences = AddonPreferences(context.applicationContext)
        debridPreferences = DebridPreferences(context.applicationContext)
        addonRepository = AddonRepository(addonPreferences)
        initialized = true

        // Cinemeta: دائماً مثبتة
        scope.launch {
            try {
                val existing = addonPreferences.addons.first()
                val urls = existing.map { it.transportUrl }.toSet()
                if (DebridKeyBuilder.CINEMETA_URL !in urls) {
                    addonRepository.installAddon(DebridKeyBuilder.CINEMETA_URL)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cinemeta install failed", e)
            }
        }
    }

    /**
     * يستدعيها SettingsScreen بعد تعديل المفاتيح.
     * يعيد تثبيت Torrentio بالرابط الجديد.
     */
    fun refreshTorrentio() {
        scope.launch {
            try {
                val rd = debridPreferences.realDebridKey.first()
                val tb = debridPreferences.torboxKey.first()
                val pm = debridPreferences.premiumizeKey.first()

                val url = DebridKeyBuilder.buildTorrentioUrl(rd, tb, pm)
                Log.d(TAG, "Refreshing Torrentio: $url")

                // احذف القديم إن وجد
                val existing = addonPreferences.addons.first()
                existing.forEach {
                    if (it.transportUrl.contains("torrentio.strem.fun")) {
                        addonRepository.removeAddon(it.transportUrl)
                    }
                }

                // أضف الجديد
                addonRepository.installAddon(url)
            } catch (e: Exception) {
                Log.e(TAG, "Torrentio refresh failed", e)
            }
        }
    }
}
