package com.rnd.mapbox.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import com.rnd.mapbox.ui.BaseActivity
import com.rnd.mapbox.ui.MainActivity
import java.util.*

class GoogleMapActivity : BaseActivity(), OnMapReadyCallback, View.OnClickListener {
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    private lateinit var binding: ActivityGoogleMapBinding

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_map)
        Places.initialize(applicationContext, BuildConfig.GOOGLE_MAPS_API_KEY)
        placesClient = Places.createClient(this)

        val font = Typeface.createFromAsset(assets, "fonts/icomoon.ttf")
        binding.taxi.typeface = font
        binding.food.typeface = font
        binding.both.typeface = font

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


    override fun onMapReady(map: GoogleMap) {
        this.map = map
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
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
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {

                it?.let {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.latitude,
                                it.longitude
                            ), DEFAULT_ZOOM.toFloat()
                        )
                    )
                    addMarker(it)
                    connectionStateMonitor?.hasNetworkConnection()?.let { connected ->
                        if (connected) {
                            fetchAddress(LatLng(it.latitude, it.longitude))
                        } else {
                            onNetworkDisconnected()
                        }
                    }
                }
            }


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
        val addresses: List<Address>?
        val geocoder = Geocoder(this, Locale.getDefault());
        binding.progress.visibility = View.GONE
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