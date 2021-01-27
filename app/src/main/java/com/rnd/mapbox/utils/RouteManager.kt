package com.rnd.mapbox.utils

import android.content.Context
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteManager(val context: Context) {

    private val onFindRouteLitener: OnFindRouteLitener = context as OnFindRouteLitener

    fun getRoute(origin: Point, destination: Point) {
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

                    onFindRouteLitener.onSuccess(response)

                }

                override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                    onFindRouteLitener.onFailed(throwable.message)
                }
            })
    }

    interface OnFindRouteLitener {
        fun onSuccess(response: Response<DirectionsResponse?>)
        fun onFailed(msg: String?)
    }
}