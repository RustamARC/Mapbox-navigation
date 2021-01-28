package com.rnd.mapbox.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import java.sql.Timestamp

@Entity(tableName = "track_navigation")
data class Navigation(
    val img: Bitmap? = null,
    val timestamp: Long = 0L,
    val currentLocation: Point,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null

) {
    override fun toString(): String {
        return "Navigation(img=$img, timestamp=$timestamp, currentLocation=$currentLocation, id=$id)"
    }
}


