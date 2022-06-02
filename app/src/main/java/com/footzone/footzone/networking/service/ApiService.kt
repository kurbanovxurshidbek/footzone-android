package com.footzone.footzone.networking.service

import com.footzone.footzone.model.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.http.*

interface ApiService {

    @GET("sms/send/forRegister/{phoneNumber}")
    suspend fun singUp(@Path("phoneNumber") phoneNumber: String): Response

    @GET("sms/send/forLogin/{phoneNumber}")
    suspend fun singIn(@Path("phoneNumber") phoneNumber: String): Response

    @POST("sms/validate/forRegister")
    suspend fun checkValidation(@Body smsVerification: SmsVerification): SmsVerificationResponse

    @POST("auth/login")
    suspend fun signInVerification(@Body signInVerification: SignInVerification): Response

    @POST("auth/register")
    suspend fun registerUser(@Body user: User): Response



    
}