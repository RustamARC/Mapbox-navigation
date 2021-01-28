package com.rnd.mapbox.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rnd.mapbox.converter.Converter
import com.rnd.mapbox.dao.NavigationDao
import com.rnd.mapbox.model.Navigation

@Database(
    entities = [Navigation::class], version = 1
)
@TypeConverters(Converter::class)
abstract class NavigationDatabase : RoomDatabase() {

    abstract fun getNavigationDao(): NavigationDao
}