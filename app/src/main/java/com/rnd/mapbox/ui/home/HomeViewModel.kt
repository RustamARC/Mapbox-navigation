package com.rnd.mapbox.ui.home

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng


class HomeViewModel : ViewModel() {

    var latLng = LatLng(22.611078, 88.439329)
}