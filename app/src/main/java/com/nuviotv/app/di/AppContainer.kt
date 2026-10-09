package com.nuviotv.app.di

import android.content.Context
import com.nuviotv.app.data.local.AddonPreferences
import com.nuviotv.app.data.repository.AddonRepository

object AppContainer {

    private var initialized = false

    lateinit var addonPreferences: AddonPreferences
        private set

    lateinit var addonRepository: AddonRepository
        private set

    fun init(context: Context) {
        if (initialized) return
        addonPreferences = AddonPreferences(context.applicationContext)
        addonRepository = AddonRepository(addonPreferences)
        initialized = true
    }
}
