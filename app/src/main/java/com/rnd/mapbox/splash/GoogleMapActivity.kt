package com.rnd.mapbox.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.rnd.mapbox.BuildConfig
import com.rnd.mapbox.R
import com.rnd.mapbox.databinding.ActivityGoogleMapBinding
import com.rnd.mapbox.ui.MainActivity
import com.rnd.mapbox.ui.NavigationActivity
import java.util.*

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    private lateinit var binding: ActivityGoogleMapBinding

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (My location) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(22.611078, 88.439329)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_map)
        // Retrieve the content view that renders the map.
//        setContentView(R.layout.activity_google_map)

        // [START_EXCLUDE silent]
        // Construct a PlacesClient
        Places.initialize(applicationContext, BuildConfig.GOOGLE_MAPS_API_KEY)
        placesClient = Places.createClient(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        // [START maps_current_place_map_fragment]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.mcvTaxi.setOnClickListener(this)
        binding.mcvFood.setOnClickListener(this)
        binding.mcvBoth.setOnClickListener(this)

        // [END maps_current_place_map_fragment]
        // [END_EXCLUDE]
    }
    // [END maps_current_place_on_create]

    /**
     * Saves the state of the map when the activity is paused.
     */
    // [START maps_current_place_on_save_instance_state]
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }
    // [END maps_current_place_on_save_instance_state]

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.current_place_menu, menu)
        return true
    }

    /* */
    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     *//*
    // [START maps_current_place_on_options_item_selected]
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option_get_place) {
            showCurrentPlace()
        }
        return true
    }*/
    // [END maps_current_place_on_options_item_selected]

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    // [START maps_current_place_on_map_ready]
    override fun onMapReady(map: GoogleMap) {
        this.map = map

        // [START_EXCLUDE]
        // [START map_current_place_set_info_window_adapter]
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        /* this.map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
             // Return null here, so that getInfoContents() is called next.
             override fun getInfoWindow(arg0: Marker): View? {
                 return null
             }

             override fun getInfoContents(marker: Marker): View {
                 // Inflate the layouts for the info window, title and snippet.
                 val infoWindow = layoutInflater.inflate(R.layout.custom_info_contents,
                     findViewById<FrameLayout>(R.id.map), false)
                 val title = infoWindow.findViewById<TextView>(R.id.title)
                 title.text = marker.title
                 val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                 snippet.text = marker.snippet
                 return infoWindow
             }
         })*/
        // [END map_current_place_set_info_window_adapter]

        // Prompt the user for permission.
        getLocationPermission()
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }
    // [END maps_current_place_on_map_ready]

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                            lastKnownLocation?.let {
                                addMarker(it)
                            }
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }

            fetchAddress(defaultLocation)
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun addMarker(lastKnownLocation: Location) {
        map?.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        lastKnownLocation.latitude,
                        lastKnownLocation.longitude
                    )
                )
        )?.setIcon(BitmapDescriptorFactory.defaultMarker())

        map?.addCircle(
            CircleOptions().center(
                LatLng(
                    lastKnownLocation.latitude,
                    lastKnownLocation.longitude
                )
            )/*.radius(500.0).strokeWidth(2.0f).fillColor(android.R.color.transparent)*/
            /* .strokeColor(R.color.mapboxBlue)*/
        )
    }

    private fun fetchAddress(defaultLocation: LatLng) {
        var geocoder: Geocoder
        val addresses: List<Address>?
        geocoder = Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(
            defaultLocation.latitude,
            defaultLocation.longitude,
            1
        ); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses.get(0).locality
        val state = addresses.get(0).adminArea
        val country = addresses.get(0).countryName
        val postalCode = addresses.get(0).postalCode
        val knownName = addresses.get(0).featureName // Only if available else return NULL

        findViewById<TextView>(R.id.tvAddress).text = address

    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_update_location_ui]

    companion object {
        private val TAG = GoogleMapActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        // [START maps_current_place_state_keys]
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        // [END maps_current_place_state_keys]

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mcv_taxi -> {
                Toast.makeText(this, "Taxi", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            R.id.mcv_food -> {
                Toast.makeText(this, "Food", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            R.id.mcv_both -> {
                Toast.makeText(this, "Both", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }

        }
    }

    private fun navigateToHome() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}