package com.rnd.mapbox.repository

import com.rnd.mapbox.dao.NavigationDao
import com.rnd.mapbox.model.Navigation
import javax.inject.Inject

class NavigationRepository @Inject constructor(
    val navigationDao: NavigationDao
) {
    suspend fun insertNavigation(navigation: Navigation) =
        navigationDao.insertNavigation(navigation)

    fun getNavigationList() = navigationDao.getNavigationList()
}