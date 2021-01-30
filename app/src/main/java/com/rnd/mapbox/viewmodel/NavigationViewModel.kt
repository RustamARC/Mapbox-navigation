package com.rnd.mapbox.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rnd.mapbox.model.Navigation
import com.rnd.mapbox.repository.NavigationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NavigationViewModel @ViewModelInject constructor(
    private val navigationRepository: NavigationRepository

) : ViewModel() {

    val mainScope = CoroutineScope(Dispatchers.Main)
    val defaultScope = CoroutineScope(Dispatchers.Default)
    val ioScope = CoroutineScope(Dispatchers.IO)

    fun saveNavigationData(navigation: Navigation) {
        viewModelScope.launch {
            navigationRepository.navigationDao.insertNavigation(
                navigation
            )
        }
    }

}