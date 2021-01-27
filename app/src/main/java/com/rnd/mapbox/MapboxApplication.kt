package com.rnd.mapbox

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MapboxApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupMapbox()
    }

    private fun setupMapbox() {
        BuildConfig.MAPBOX_ACCESS_TOKEN.let {
            Mapbox.getInstance(applicationContext, it)
        }

    }

}