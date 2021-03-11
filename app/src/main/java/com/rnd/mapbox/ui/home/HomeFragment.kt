package com.rnd.mapbox.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.MarginPageTransformer
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.*
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.rnd.mapbox.BuildConfig
import com.rnd.mapbox.R
import com.rnd.mapbox.databinding.FragmentHomeBinding
import com.rnd.mapbox.ui.home.viewpager.ViewPagerAdapter
import com.rnd.mapbox.utils.toLatLng
import com.rnd.mapbox.utils.toPoint
import com.rnd.mapbox.utils.toast
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), OnMapReadyCallback,
    LocationEngineCallback<LocationEngineResult>, PermissionsListener,
    MapboxMap.OnMapClickListener, OnLocationClickListener, OnCameraTrackingChangedListener {

    private var currentRoute: DirectionsRoute? = null
    private lateinit var lastKnownLocation: Location
    private lateinit var homeViewModel: HomeViewModel
    lateinit var binding: FragmentHomeBinding
    lateinit var mapInterationListener: MapInterationListener
    private var mapboxMap: MapboxMap? = null
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    lateinit var mapView: MapView
    private var locationComponent: LocationComponent? = null
    private var isInTrackingMode = false

    // The entry point to the Fused Location Provider.
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        Mapbox.getInstance(requireContext(), BuildConfig.MAPBOX_ACCESS_TOKEN)
        binding.mapView.getMapAsync(this)
        binding.mapView.onCreate(savedInstanceState)
        val font = Typeface.createFromAsset(requireContext().assets, "fonts/icomoon.ttf")
        binding.btnMyLocation.typeface = font
        binding.btnSafety.typeface = font

        val ssb = SpannableStringBuilder("$0.00")
        val dollarColor = ForegroundColorSpan(resources.getColor(R.color.colorDollar))
        ssb.setSpan(dollarColor, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.btnRate.text = ssb
        binding.notifyPropertyChanged(R.id.btnRate)

        mapInterationListener.onReset()

        binding.btnGo.setOnClickListener {
            mapInterationListener.onGoClicked()
        }

        binding.btnNavigate.setOnClickListener {


            val taxiStarted = (activity as com.rnd.mapbox.ui.MainActivity).isStarted
            if (!taxiStarted) {
                mapInterationListener.onNavigate(taxiStarted, null)
            } else {

                lifecycleScope.launch {
                    getRoute()
                }


            }

        }

        binding.btnSafety.setOnClickListener {
            mapInterationListener.onSafetyClicked()

        }

        binding.btnMyLocation.setOnClickListener {
            onLocationComponentClick()
        }

        binding.btnRate.setOnClickListener {
            binding.rateVP.visibility = View.VISIBLE
            binding.btnRate.visibility = View.GONE
            binding.rateVP.setPageTransformer(MarginPageTransformer(5))
            binding.rateVP.adapter = ViewPagerAdapter(requireContext())

        }

        return binding.root
    }

    private fun getRoute() {
        mapboxMap?.locationComponent?.lastKnownLocation?.let {
            val destinationPoint: Point = Point.fromLngLat(88.3423735, 22.583256)
            val originPoint: Point = it.toPoint()
            val source: GeoJsonSource =
                mapboxMap?.style?.getSourceAs("destination-source-id")!!
            source.setGeoJson(Feature.fromGeometry(destinationPoint))
            getRoute(originPoint, destinationPoint)
        }

    }

    private fun getRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(context)
            .accessToken(Mapbox.getAccessToken()!!)
            .origin(origin)
            .destination(destination)
            .build()
            .getRoute(object : Callback<DirectionsResponse?> {
                override fun onResponse(
                    call: Call<DirectionsResponse?>,
                    response: Response<DirectionsResponse?>
                ) {
                    if (response.body() == null) {
                        toast("No routes found, make sure you set the right user and access token.")
                    } else if (response.body()!!.routes().size < 1) {
                        toast("No routes found")
                        return
                    }

                    currentRoute = response.body()!!.routes()[0]
                    binding.btnNavigate.visibility = View.GONE
                    mapInterationListener.onNavigate(true, currentRoute)
                }

                override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                    throwable.message?.let { toast(it) }
                }
            })
    }

    override fun onMapClick(latLng: LatLng): Boolean {
        onMapTouch()
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MapInterationListener) {
            mapInterationListener = context
        } else {
            throw RuntimeException("Must implement MapInterationListener in your activity")
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap?.setStyle(Style.MAPBOX_STREETS) { style ->
            enableLocationComponent(style)
            addDestinationIconSymbolLayer(style)
        }
        this.mapboxMap?.addOnMapClickListener(this)
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
            PropertyFactory.iconImage("destination-icon-id"),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )
        loadedMapStyle.addLayer(destinationSymbolLayer)
    }


    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(@NonNull loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            locationComponent = mapboxMap?.locationComponent?.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)
                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true
                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
                addOnLocationClickListener(this@HomeFragment)
                addOnCameraTrackingChangedListener(this@HomeFragment)
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(requireActivity())
        }
    }

    override fun onSuccess(result: LocationEngineResult?) {
        result?.lastLocation?.let {
            lastKnownLocation = it
            mapboxMap?.locationComponent?.forceLocationUpdate(it)
            animateCamera(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        toast("Required location permission")
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap?.style!!)
        } else {
            toast("Location permission not granted")
        }
    }

    private fun animateCamera(location: Location) {

        val position: CameraPosition = CameraPosition.Builder()
            .target(location.toLatLng()) // Sets the new camera position
            .zoom(12.0) // Sets the zoom
            /*  .bearing(180.0) // Rotate the camera
              .tilt(30.0) // Set the camera tilt*/
            .build() // Creates a CameraPosition from the builder
        mapboxMap?.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(position), 2000
        )
    }

    override fun onFailure(exception: Exception) {
        exception.message?.let { toast(it) }
    }


    private fun onMapTouch() {
        binding.btnRate.visibility = View.VISIBLE
        binding.rateVP.visibility = View.GONE
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
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    interface MapInterationListener {
        fun onGoClicked()
        fun onSafetyClicked()
        fun onReset()
        fun onNavigate(isStarted: Boolean, currentRoute: DirectionsRoute?)
    }

    override fun onLocationComponentClick() {
        locationComponent?.lastKnownLocation?.let {
            val position: CameraPosition = CameraPosition.Builder()
                .target(it.toLatLng()) // Sets the new camera position
                .zoom(15.0) // Sets the zoom
                /*  .bearing(180.0) // Rotate the camera
                  .tilt(30.0) // Set the camera tilt*/
                .build() // Creates a CameraPosition from the builder
            mapboxMap?.animateCamera(
                CameraUpdateFactory
                    .newCameraPosition(position), 1000
            )
        }
    }


    override fun onCameraTrackingDismissed() {
        isInTrackingMode = false
    }

    override fun onCameraTrackingChanged(currentMode: Int) {
// Empty on purpose
    }
}