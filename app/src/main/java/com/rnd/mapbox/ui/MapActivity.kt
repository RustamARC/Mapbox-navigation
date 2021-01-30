package com.rnd.mapbox.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.snapshotter.MapSnapshotter
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.rnd.mapbox.R
import com.rnd.mapbox.constant.Constant.REQUEST_CODE_LOCATION_PERMISSION
import com.rnd.mapbox.databinding.ActivityMapBinding
import com.rnd.mapbox.listener.ClickHandler
import com.rnd.mapbox.utils.EasyPermissionHandler
import com.rnd.mapbox.utils.RouteManager
import com.rnd.mapbox.utils.toLatLng
import com.rnd.mapbox.utils.toPoint
import com.rnd.mapbox.viewmodel.MainViewModel
import com.rnd.mapbox.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Response
import javax.inject.Inject


@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener,
    EasyPermissions.PermissionCallbacks, LocationEngineCallback<LocationEngineResult?>,
    RouteManager.OnFindRouteLitener, ClickHandler, OnNavigationReadyCallback, NavigationListener {
    var hasStartedSnapshotGeneration: Boolean = false
    lateinit var mapboxMap: MapboxMap
    private var currentRoute: DirectionsRoute? = null
    var navigationMapRoute: NavigationMapRoute? = null
    private var mapSnapshotter: MapSnapshotter? = null
    private val TAG = MapActivity::class.java.simpleName

    @Inject
    lateinit var locationEngine: LocationEngine

    @Inject
    lateinit var locationEngineRequest: LocationEngineRequest
    private val mainViewModel: MainViewModel by viewModels()

    private val navigationViewModel: NavigationViewModel by viewModels()
    private lateinit var binding: ActivityMapBinding

    private lateinit var navigationMapboxMap: NavigationMapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        binding.handlers = this
        binding.viewModel = mainViewModel
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        requestPermission()
    }

    fun setCameraPosition(location: Location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(), 8.0))
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mainViewModel.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
            addDestinationIconSymbolLayer(it)
            this.mapboxMap.addOnMapClickListener(this@MapActivity)
        }
    }


    override fun onStartNavigationClick(view: View) {
        currentRoute?.let {
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra("route", it.toJson())
            startActivity(intent)

            /*   val simulateRoute = true
               val options = NavigationLauncherOptions.builder()
                   .directionsRoute(currentRoute)
                   .shouldSimulateRoute(simulateRoute)
                   .build()
               NavigationLauncher.startNavigation(this@MapActivity, options)*/
        }
    }

    private fun addDestinationIconSymbolLayer(loadedMapStyle: Style) {
        loadedMapStyle.addImage(
            "destination-icon-id",
            BitmapFactory.decodeResource(
                this.resources,
                R.drawable.mapbox_marker_icon_default
            )
        )
        val geoJsonSource = GeoJsonSource("destination-source-id")
        loadedMapStyle.addSource(geoJsonSource)
        val destinationSymbolLayer =
            SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(
            iconImage("destination-icon-id"),
            iconAllowOverlap(true),
            iconIgnorePlacement(true)
        )
        loadedMapStyle.addLayer(destinationSymbolLayer)
    }

    override fun onMapClick(latLng: LatLng): Boolean {
        mapboxMap.locationComponent.lastKnownLocation?.let {
            val destinationPoint: Point = latLng.toPoint()
            val originPoint: Point = it.toPoint()
            val source: GeoJsonSource =
                mapboxMap.style?.getSourceAs("destination-source-id")!!
            source.setGeoJson(Feature.fromGeometry(destinationPoint))
            val navigationMapRoute = RouteManager(this)
            navigationMapRoute.getRoute(originPoint, destinationPoint)
            mainViewModel.currentRoute = null
            binding.viewModel = mainViewModel
            binding.invalidateAll()
        }
        return true
    }

    private fun requestPermission() {
        if (EasyPermissionHandler.hasLocationPermission(this)) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to have location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to have location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */

    private fun enableLocationComponent(@NonNull loadedMapStyle: Style) {
        if (EasyPermissionHandler.hasLocationPermission(this)) {
            val locationComponent = mapboxMap.locationComponent
            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .useDefaultLocationEngine(false)
                    .build()
            locationComponent.activateLocationComponent(locationComponentActivationOptions)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.GPS

            initLocationEngine()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        // Make sure to stop the snapshotter on pause if it exists
        if (mapSnapshotter != null) {
            mapSnapshotter?.cancel();
        }
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationEngine.removeLocationUpdates(this)
        binding.mapView.onDestroy()
    }


    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine.requestLocationUpdates(locationEngineRequest, this, mainLooper)
        locationEngine.getLastLocation(this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionDenied(this, perms.toString())) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (mapboxMap.style != null) {
            enableLocationComponent(mapboxMap.style!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }


    override fun onSuccess(result: LocationEngineResult?) {
        result?.lastLocation?.let {
            mapboxMap.locationComponent.forceLocationUpdate(it)
        }
    }

    override fun onFailure(exception: Exception) {
        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(response: Response<DirectionsResponse?>) {


        if (response.body() == null) {
            Toast.makeText(
                this@MapActivity,
                "No routes found, make sure you set the right user and access token.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (response.body()!!.routes().size < 1) {
            Toast.makeText(
                this@MapActivity,
                "No routes found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        currentRoute = response.body()!!.routes()[0]
        mainViewModel.currentRoute = currentRoute
        binding.viewModel = mainViewModel
        binding.invalidateAll()
        if (navigationMapRoute != null) {
            navigationMapRoute?.removeRoute()
        } else {
            navigationMapRoute =
                NavigationMapRoute(
                    null,
                    binding.mapView,
                    mapboxMap,
                    R.style.NavigationMapRoute
                )
        }
        navigationMapRoute?.addRoute(currentRoute)


    }

    override fun onFailed(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    override fun onNavigationFinished() {
        Log.e(TAG, "onNavigationFinished: ")
    }

    override fun onNavigationRunning() {
        Log.e(TAG, "onNavigationRunning: ")
    }

    override fun onCancelNavigation() {
        Log.e(TAG, "onCancelNavigation: ")
    }

    override fun onNavigationReady(isRunning: Boolean) {
        Log.e(TAG, "onNavigationReady: ${isRunning}")
    }

}