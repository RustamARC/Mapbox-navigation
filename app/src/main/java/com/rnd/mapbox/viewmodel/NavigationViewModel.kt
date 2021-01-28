package com.rnd.mapbox.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.rnd.mapbox.repository.NavigationRepository

class NavigationViewModel @ViewModelInject constructor(
    val navigationRepository: NavigationRepository

) : ViewModel() {

}