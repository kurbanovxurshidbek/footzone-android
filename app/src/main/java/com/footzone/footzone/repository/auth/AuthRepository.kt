package com.footzone.footzone.repository.auth

import com.footzone.footzone.model.SignInVerification
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

    suspend fun signUp(phoneNumber: String) = apiService.singUp(phoneNumber)

    suspend fun signIn(phoneNumber: String) = apiService.singIn(phoneNumber)

    suspend fun checkValidation(smsVerification: SmsVerification) = apiService.checkValidation(smsVerification)

    suspend fun signInVerification(signInVerification: SignInVerification) = apiService.signInVerification(signInVerification)

    suspend fun registerUser(user: User) = apiService.registerUser(user)

}