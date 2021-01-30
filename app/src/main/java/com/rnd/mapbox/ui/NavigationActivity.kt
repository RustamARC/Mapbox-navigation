package com.rnd.mapbox.ui


import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.ui.v5.NavigationSnapshotReadyCallback
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap
import com.mapbox.services.android.navigation.v5.location.RawLocationListener
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.rnd.mapbox.BuildConfig
import com.rnd.mapbox.R
import com.rnd.mapbox.model.Navigation
import com.rnd.mapbox.utils.toLatLng
import com.rnd.mapbox.utils.toPoint
import com.rnd.mapbox.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener,
    RouteListener,
    ProgressChangeListener, RawLocationListener {

    private lateinit var navigationMapboxMap: NavigationMapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var route: DirectionsRoute /*by lazy { getDirectionsRoute() }*/
    private val navigationViewModel: NavigationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, BuildConfig.MAPBOX_ACCESS_TOKEN)
        setContentView(R.layout.activity_navigation)
        navigationView.onCreate(savedInstanceState)
        navigationView.initialize(this)
        route = DirectionsRoute.fromJson(intent.getStringExtra("route"))
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        navigationView.onStart()
    }

    override fun onResume() {
        super.onResume()
        navigationView.onResume()
    }

    override fun onStop() {
        super.onStop()
        navigationView.onStop()
    }

    override fun onPause() {
        super.onPause()
        navigationView.onPause()
    }

    override fun onDestroy() {
        navigationView.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationView.onRestoreInstanceState(savedInstanceState)
    }

    override fun onNavigationReady(isRunning: Boolean) {
        if (!isRunning && !::navigationMapboxMap.isInitialized) {
            if (navigationView.retrieveNavigationMapboxMap() != null) {
                this.navigationMapboxMap = navigationView.retrieveNavigationMapboxMap()!!
                navigationView.retrieveMapboxNavigation()?.let {
                    this.mapboxNavigation = it
                }
                val optionsBuilder = NavigationViewOptions.builder()
                optionsBuilder.navigationListener(this)
                optionsBuilder.directionsRoute(route)
                optionsBuilder.shouldSimulateRoute(true)
                optionsBuilder.routeListener(this)
                navigationView.startNavigation(optionsBuilder.build())

            }
        }
    }

    override fun onNavigationRunning() {
        Log.e(TAG, "onNavigationRunning: ")
        navigationView.retrieveMapboxNavigation()?.let {
            it.addRawLocationListener(this)
        }

    }

    override fun onNavigationFinished() {
        finish()
    }

    override fun onCancelNavigation() {
        navigationView.stopNavigation()
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onFailedReroute(errorMessage: String?) {
        Log.e(TAG, "onFailedReroute: ${errorMessage}")
    }

    override fun allowRerouteFrom(offRoutePoint: Point?): Boolean {
        Log.e(TAG, "allowRerouteFrom: ${offRoutePoint?.toJson()}")
        return true
    }

    override fun onRerouteAlong(directionsRoute: DirectionsRoute?) {
        Log.e(TAG, "onRerouteAlong: ${directionsRoute.toString()}")
    }

    override fun onOffRoute(offRoutePoint: Point?) {
        Log.e(TAG, "onOffRoute: ${offRoutePoint?.toJson()}")
    }

    override fun onArrival() {
        Log.e(TAG, "onArrival")
    }

    override fun onProgressChange(location: Location?, routeProgress: RouteProgress?) {
        Log.e(
            TAG,
            "onProgressChange: ${location?.toPoint()
                ?.toJson()}-> RouteProgress: ${routeProgress?.distanceRemaining()}"
        )
    }

    override fun onLocationUpdate(rawLocation: Location?) {
        val navigation = Navigation(
            id = 0,
            timestamp = System.currentTimeMillis(),
            latitude = rawLocation!!.latitude,
            longitude = rawLocation.longitude,
            altitude = rawLocation.altitude
        )
        navigationViewModel.saveNavigationData(navigation = navigation)
    }

}
