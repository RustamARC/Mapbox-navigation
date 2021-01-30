package com.rnd.mapbox.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import java.sql.Timestamp

@Entity(tableName = "navigation_location")
data class Navigation(
    /*val img: Bitmap? = null,*/
    @PrimaryKey(autoGenerate = true)
    @Expose
    var id: Long = 0,
    var timestamp: Long = 0L,
    var latitude: Double,
    var longitude: Double,
    var altitude: Double


) {

    override fun toString(): String {
        return "Navigation(timestamp=$timestamp, currentLocation[=${latitude},${longitude},${altitude}])"
    }
}


