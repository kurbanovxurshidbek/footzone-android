package com.footzone.footzone.repository.main

import com.footzone.footzone.model.User
import com.footzone.footzone.networking.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

class MainRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getUserData(userId: String) = apiService.getUserData(userId)
    suspend fun updateUserProfilePhoto(userId: String, file: MultipartBody.Part) = apiService.updateUserProfilePhoto(userId,file)
}