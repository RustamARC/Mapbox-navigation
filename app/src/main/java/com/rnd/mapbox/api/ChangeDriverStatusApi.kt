package com.rnd.mapbox.api

import com.rnd.mapbox.api.response.StatusChangeResponse
import com.rnd.mapbox.constant.Constant.CHANGE_DRIVER_STATUS
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ChangeDriverStatusApi {

    @POST(CHANGE_DRIVER_STATUS)
    suspend fun changeDriverStatus(
        @Body requestBody: RequestBody
    ): StatusChangeResponse
}