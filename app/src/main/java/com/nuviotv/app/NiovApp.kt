package com.nuviotv.app

import android.app.Application
import com.nuviotv.app.di.AppContainer

class NiovApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
