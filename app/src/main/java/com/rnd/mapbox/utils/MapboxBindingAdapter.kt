package com.rnd.mapbox.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


@BindingAdapter("navigation:enable")
fun enableNavigationButton(view: View, enable: Boolean) {
    view.visibility = if (enable) View.VISIBLE else View.GONE
}

@BindingAdapter("progress:enable")
fun showProgress(view: View, enable: Boolean) {
    view.visibility = if (enable) View.VISIBLE else View.GONE
}


@BindingAdapter("initMap")
fun initMap(mapView: MapView?, latLng: LatLng?) {
    if (mapView != null) {
        mapView.onCreate(Bundle())

        mapView.getMapAsync(OnMapReadyCallback { googleMap ->
            googleMap.addMarker(latLng?.let {
                MarkerOptions().position(it).title("Your location")
            })
        })
    }
}