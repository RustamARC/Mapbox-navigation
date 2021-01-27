package com.rnd.mapbox.utils

import android.location.Location
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng

fun Point.toLocation(): Location {
    val location = Location("")
    location.longitude = this.longitude()
    location.latitude = this.latitude()
    return location
}

fun Location.toPoint(): Point {
    return Point.fromLngLat(this.longitude, this.latitude)
}

fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun LatLng.toPoint(): Point {
    return Point.fromLngLat(this.longitude, this.latitude)
}

fun Point.toLatLng(): LatLng {
    return LatLng(this.latitude(), this.longitude())
}