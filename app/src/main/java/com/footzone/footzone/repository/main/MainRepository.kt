package com.footzone.footzone.repository.main

import com.footzone.footzone.model.Location
import com.footzone.footzone.model.User
import com.footzone.footzone.networking.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class MainRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getNearByStadiums(location: Location) =
        apiService.getNearByStadiums(location)

    suspend fun getFavouriteStadiums(userId: String) = apiService.getFavouriteStadiums(userId)
}