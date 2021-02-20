package com.rnd.mapbox.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rnd.mapbox.R
import com.rnd.mapbox.databinding.FragmentHomeBinding
import com.rnd.mapbox.utils.PermissionUtils
import com.rnd.mapbox.utils.toLatLng

class HomeFragment : Fragment(), OnMapReadyCallback,
    LocationListener {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var homeViewModel: HomeViewModel
    lateinit var binding: FragmentHomeBinding
    private var desMarker: Marker? = null
    private var googleMap: GoogleMap? = null

    // The entry point to the Fused Location Provider.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
//        binding.viewModel = homeViewModel
        // Construct a FusedLocationProviderClient.
        binding.map.getMapAsync(this)
        binding.map.onCreate(savedInstanceState)
        binding.btnNavigate.setOnClickListener {


        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (PermissionUtils.isAccessFineLocationGranted(requireContext())) {
            val locationClient = fusedLocationClient
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
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
            locationClient?.lastLocation?.addOnSuccessListener {
                navigateCamera(it)
            }
        }
    }


    private fun navigateCamera(it: Location?) {
        it?.let {
            googleMap?.addMarker(
                MarkerOptions().position(
                    LatLng(
                        it.latitude,
                        it.longitude
                    )
                )
            )?.title = "Your location!"

            val cameraPosition: CameraPosition =
                CameraPosition.Builder().target(
                    LatLng(
                        it.latitude,
                        it.longitude
                    )
                ).zoom(18f).build()
            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        }


    }


    override fun onMapReady(gMap: GoogleMap?) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
        googleMap = gMap
        googleMap?.isMyLocationEnabled = true

        googleMap?.setOnMapClickListener {
            desMarker?.remove()
            desMarker =
                googleMap?.addMarker(MarkerOptions().position(it).title("Your Destination"))


        }

    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onLocationChanged(p0: Location?) {
        Log.e("onLocationChanged: ", p0?.toLatLng().toString())
    }
}