package com.rnd.mapbox.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.rnd.mapbox.repository.MainRepository
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(private val mainRepository: MainRepository) :
    ViewModel(),
    OnMapReadyCallback, MapboxMap.OnMapClickListener {

    lateinit var mapboxMap: MapboxMap
    var mapView: MapView? = null

    fun initLocationEngine(application: Application) {
        mainRepository.initLocationEngine(application)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS) {

        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        return false
    }


}