package com.rnd.mapbox.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.rnd.mapbox.api.ChangeDriverStatusApi
import com.rnd.mapbox.api.response.StatusChangeResponse
import com.rnd.mapbox.datasource.RemoteDataSource
import com.rnd.mapbox.network.Resource
import com.rnd.mapbox.repository.ChangeDriverStatusRepository
import com.rnd.mapbox.repository.MainRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class MainActivityViewModel() : ViewModel() {
    private val dataSource = RemoteDataSource()
    private val changeDriverStatusRepository = ChangeDriverStatusRepository(
        dataSource.buildApi(
            ChangeDriverStatusApi::class.java
        )
    )
    private val _statusChangeResponse: MutableLiveData<Resource<StatusChangeResponse>> =
        MutableLiveData()
    val statusChangeResponse: LiveData<Resource<StatusChangeResponse>>
        get() = _statusChangeResponse

    fun changeDriverStatus() {
        val jsonObj = JSONObject()
        val jObj = JSONObject()
        try {
            jObj.put("cab_driver_Id", 164)
            jObj.put("cab_driver_Status", "Online")
            jsonObj.put("inputData", jObj)
        } catch (e: Exception) {
        }
        val requestBody =
            jsonObj.toString().toRequestBody("application/json".toMediaTypeOrNull())

        viewModelScope.launch {
            _statusChangeResponse.value = Resource.Loading
            _statusChangeResponse.value =
                changeDriverStatusRepository.changeDriverStatus(requestBody)
        }
    }
}