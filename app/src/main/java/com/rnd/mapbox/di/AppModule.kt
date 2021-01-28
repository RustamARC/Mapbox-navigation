package com.rnd.mapbox.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.rnd.mapbox.R
import com.rnd.mapbox.constant.Constant
import com.rnd.mapbox.db.NavigationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Appendable
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesNavigationDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, NavigationDatabase::class.java, Constant.DATABASE_NAME).build()

    @Singleton
    @Provides
    fun providesNavigationDao(navigationDatabase: NavigationDatabase) =
        navigationDatabase.getNavigationDao()

    @Singleton
    @Provides
    fun providesLocationEngineRequest(application: Application): LocationEngineRequest {
        return LocationEngineRequest.Builder(Constant.DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(Constant.DEFAULT_MAX_WAIT_TIME).build()
    }

    @Singleton
    @Provides
    fun providesLocationEngine(application: Application): LocationEngine {
        return LocationEngineProvider.getBestLocationEngine(application)
    }

    @Singleton
    @Provides
    fun providesNavigationRoute(
        application: Application,
        origin: Point,
        destination: Point
    ): NavigationRoute {
        return NavigationRoute.builder(application.applicationContext)
            .accessToken(Mapbox.getAccessToken()!!)
            .origin(origin)
            .destination(destination)
            .build()

    }

    @Singleton
    @Provides
    fun providesNavigationMapRoute(
        mapView: MapView,
        mapboxmap: MapboxMap
    ): NavigationMapRoute {
        return NavigationMapRoute(
            null,
            mapView,
            mapboxmap,
            R.style.NavigationMapRoute
        )
    }
}