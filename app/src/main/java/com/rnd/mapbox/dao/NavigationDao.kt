package com.rnd.mapbox.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rnd.mapbox.model.Navigation

@Dao
interface NavigationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNavigation(navigation: Navigation)

    @Delete
    suspend fun deleteNavigation(navigation: Navigation)

    @Query("Select * from navigation_location ORDER BY timestamp DESC")
    fun getNavigationList(): LiveData<List<Navigation>>

}