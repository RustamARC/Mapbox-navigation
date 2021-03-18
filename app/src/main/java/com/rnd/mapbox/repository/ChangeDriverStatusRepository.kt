package com.rnd.mapbox.repository

import com.rnd.mapbox.api.ChangeDriverStatusApi
import okhttp3.RequestBody

class ChangeDriverStatusRepository(private val api: ChangeDriverStatusApi) : BaseRepository() {

    suspend fun changeDriverStatus(requestBody: RequestBody) = safeApiCall {
        api.changeDriverStatus(requestBody)
    }
}