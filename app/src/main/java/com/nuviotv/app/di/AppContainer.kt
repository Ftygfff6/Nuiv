package com.nuviotv.app.di

import android.content.Context
import com.nuviotv.app.data.local.AddonPreferences
import com.nuviotv.app.data.repository.AddonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object AppContainer {
    private var initialized = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var addonPreferences: AddonPreferences
        private set
    lateinit var addonRepository: AddonRepository
        private set

    private val DEFAULT_ADDONS = listOf(
        "https://v3-cinemeta.strem.io"
    )

    fun init(context: Context) {
        if (initialized) return
        addonPreferences = AddonPreferences(context.applicationContext)
        addonRepository = AddonRepository(addonPreferences)
        initialized = true

        scope.launch {
            try {
                val existing = addonPreferences.addons.first()
                val urls = existing.map { it.transportUrl }.toSet()
                DEFAULT_ADDONS.forEach { url ->
                    if (url !in urls) {
                        addonRepository.installAddon(url)
                    }
                }
            } catch (e: Exception) { }
        }
    }
}
