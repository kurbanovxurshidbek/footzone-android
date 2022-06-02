package com.footzone.footzone.repository.auth

import com.footzone.footzone.model.SmsVerification
import com.footzone.footzone.model.User
import com.footzone.footzone.networking.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun signUp(string: String) = apiService.singUp(string)

    suspend fun checkValidation(smsVerification: SmsVerification) = apiService.checkValidation(smsVerification)

    suspend fun registerUser(user: User) = apiService.registerUser(user)
}