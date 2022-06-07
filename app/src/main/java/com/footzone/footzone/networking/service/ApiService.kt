package com.footzone.footzone.networking.service

import com.footzone.footzone.model.*
import com.footzone.footzone.model.holders.HolderStadiumResponse
import com.footzone.footzone.model.playhistory.PlayHistoryResponse
import com.footzone.footzone.model.profile.UserData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MultipartBody
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

    @POST("stadium/viewNearStadiums")
    suspend fun getNearByStadiums(@Body location: Location): StadiumResponse

    @GET("favorites/{userId}")
    suspend fun getFavouriteStadiums(@Path("userId") userId: String): StadiumResponse

    @Headers("Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIrOTk4OTAzNDExNTExIiwiaWF0IjoxNjU0MDY3MzU5LCJleHAiOjE2NTY2NTkzNTksInJvbGVzIjpbeyJpZCI6Ijg0OGQxNjkwLWIzNDUtNDgxZC1iMDBiLTY0YmNjYTM2NzVhYiIsImNyZWF0ZWRBdCI6MTY1NDA0MTUyNDg4NCwidXBkYXRlZEF0IjoxNjU0MDQxNTI0ODg0LCJjcmVhdGVkQnkiOm51bGwsInVwZGF0ZWRCeSI6bnVsbCwibmFtZSI6IlVzZXIiLCJkZXNjcmlwdGlvbiI6IlVzZXIifV19.wk4mhvMv6gW-SOrbt1wcl6jUOldZPZeKo2e-S3BanoCm8yXCq8V9ukaOb7MTh_qroCNGvAq_dvu80YXu-8G9Gg")
    @GET("user/{userId}")
    suspend fun getUserData(@Path("userId") userId: String): UserData

    @GET("stadium/history/{userId}")
    suspend fun getUserPlayHistory(@Path("userId") userId: String): PlayHistoryResponse

    @Headers("Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIrOTk4OTAzNDExNTExIiwiaWF0IjoxNjU0MDY3MzU5LCJleHAiOjE2NTY2NTkzNTksInJvbGVzIjpbeyJpZCI6Ijg0OGQxNjkwLWIzNDUtNDgxZC1iMDBiLTY0YmNjYTM2NzVhYiIsImNyZWF0ZWRBdCI6MTY1NDA0MTUyNDg4NCwidXBkYXRlZEF0IjoxNjU0MDQxNTI0ODg0LCJjcmVhdGVkQnkiOm51bGwsInVwZGF0ZWRCeSI6bnVsbCwibmFtZSI6IlVzZXIiLCJkZXNjcmlwdGlvbiI6IlVzZXIifV19.wk4mhvMv6gW-SOrbt1wcl6jUOldZPZeKo2e-S3BanoCm8yXCq8V9ukaOb7MTh_qroCNGvAq_dvu80YXu-8G9Gg")
    @Multipart
    @POST("user/changeProfilePicture/{userId}")
    suspend fun updateUserProfilePhoto(
        @Path("userId") userId: String,
        @Part file: MultipartBody.Part
    ): String


    //not yet fully connected
    @GET("stadium/{stadiumId}")
     fun getPitchData(@Path("stadiumId") stadiumId: String): Response

    @Headers("Authorization:Bearer 9eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIrOTk4OTAzNDExNTExIiwiaWF0IjoxNjU0MDY3MzU5LCJleHAiOjE2NTY2NTkzNTksInJvbGVzIjpbeyJpZCI6Ijg0OGQxNjkwLWIzNDUtNDgxZC1iMDBiLTY0YmNjYTM2NzVhYiIsImNyZWF0ZWRBdCI6MTY1NDA0MTUyNDg4NCwidXBkYXRlZEF0IjoxNjU0MDQxNTI0ODg0LCJjcmVhdGVkQnkiOm51bGwsInVwZGF0ZWRCeSI6bnVsbCwibmFtZSI6IlVzZXIiLCJkZXNjcmlwdGlvbiI6IlVzZXIifV19.wk4mhvMv6gW-SOrbt1wcl6jUOldZPZeKo2e-S3BanoCm8yXCq8V9ukaOb7MTh_qroCNGvAq_dvu80YXu-8G9Gg")
    @GET("stadium/holder/{userId}")
    suspend fun getHolderStadiums(@Path ("userId") userId: String): HolderStadiumResponse
}