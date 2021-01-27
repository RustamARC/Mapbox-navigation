package com.rnd.mapbox.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.rnd.mapbox.R
import com.rnd.mapbox.constant.Constant.REQUEST_CODE_LOCATION_PERMISSION
import com.rnd.mapbox.utils.EasyPermissionHandler
import com.rnd.mapbox.utils.RouteManager
import com.rnd.mapbox.utils.toLatLng
import com.rnd.mapbox.utils.toPoint
import com.rnd.mapbox.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Response
import javax.inject.Inject


@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener,
    EasyPermissions.PermissionCallbacks, LocationEngineCallback<LocationEngineResult?>,
    RouteManager.OnFindRouteLitener {
    lateinit var mapboxMap: MapboxMap
    private var mapView: MapView? = null
    private var currentRoute: DirectionsRoute? = null
    var navigationMapRoute: NavigationMapRoute? = null
    private lateinit var button: FloatingActionButton
    private var permissionsManager: PermissionsManager? = null

    @Inject
    lateinit var locationEngine: LocationEngine

    @Inject
    lateinit var locationEngineRequest: LocationEngineRequest
    private val mainViewModel: MainViewModel by viewModels()
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        requestPermission()
    }

    fun setCameraPosition(location: Location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location.toLatLng(), 8.0))
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
            addDestinationIconSymbolLayer(it)
            this.mapboxMap.addOnMapClickListener(this@MapActivity)
            button = findViewById<FloatingActionButton>(R.id.startButton)
            button.setOnClickListener {
                currentRoute?.let {
                    val simulateRoute = true
                    val options: NavigationLauncherOptions = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build()
                    NavigationLauncher.startNavigation(this@MapActivity, options)
                }

            }
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
            val source: GeoJsonSource = mapboxMap.style?.getSourceAs("destination-source-id")!!
            source.setGeoJson(Feature.fromGeometry(destinationPoint))
            val navigationMapRoute = RouteManager(this)
            navigationMapRoute.getRoute(originPoint, destinationPoint)
            button.isEnabled = true
            button.setBackgroundResource(android.R.color.darker_gray)
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
        if (EasyPermissionHandler.hasLocationPermission(this)) {// Get an instance of the component
            val locationComponent = mapboxMap.locationComponent
// Set the LocationComponent activation options
            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .useDefaultLocationEngine(false)
                    .build()
// Activate with the LocationComponentActivationOptions object
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
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    /*  override fun onPermissionResult(granted: Boolean) {
          if (granted) {
              if (mapboxMap.style != null) {
                  enableLocationComponent(mapboxMap.style!!)
              }
          } else {
              Toast.makeText(
                  this,
                  R.string.user_location_permission_not_granted, Toast.LENGTH_LONG
              )
                  .show()
              finish()
          }
      }*/

    override fun onSuccess(result: LocationEngineResult?) {
        result?.lastLocation?.let {
            mapboxMap.locationComponent.forceLocationUpdate(it)
//            setCameraPosition(it)
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
            button.isEnabled = false
            button.setBackgroundResource(android.R.color.darker_gray)
        } else if (response.body()!!.routes().size < 1) {
            Toast.makeText(
                this@MapActivity,
                "No routes found",
                Toast.LENGTH_SHORT
            ).show()
            button.isEnabled = false
            button.setBackgroundResource(android.R.color.darker_gray)
            return
        }
        currentRoute = response.body()!!.routes()[0]
        if (navigationMapRoute != null) {
            navigationMapRoute?.removeRoute()
        } else {
            navigationMapRoute =
                NavigationMapRoute(
                    null,
                    mapView!!,
                    mapboxMap,
                    R.style.NavigationMapRoute
                )
        }
        navigationMapRoute?.addRoute(currentRoute)
    }

    override fun onFailed(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}