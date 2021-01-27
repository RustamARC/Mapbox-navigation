package com.rnd.mapbox.repository

import android.annotation.SuppressLint
import android.app.Application
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import java.lang.Exception
import javax.inject.Inject

class MainRepository @Inject constructor(
    val locationEngine: LocationEngine,
    val request: LocationEngineRequest
) : LocationEngineCallback<LocationEngineResult> {


    @SuppressLint("MissingPermission")
    fun initLocationEngine(application: Application) {
        locationEngine.requestLocationUpdates(request, this, application.mainLooper)
        locationEngine.getLastLocation(this)
    }

    override fun onSuccess(result: LocationEngineResult?) {
    }

    override fun onFailure(exception: Exception) {
    }
}