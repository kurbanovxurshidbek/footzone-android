package com.footzone.footzone.networking.service

import com.footzone.footzone.model.*
import retrofit2.http.*

interface ApiService {

    @GET("sms/send/forRegister/{phoneNumber}")
    suspend fun singUp(@Path("phoneNumber") phoneNumber: String): Response

    @POST("sms/validate/forRegister")
    suspend fun checkValidation(@Body smsVerification: SmsVerification): SmsVerificationResponse

    @POST("auth/register")
    suspend fun registerUser(@Body user: User): Response

    @Headers("Authorization:Bearer 9eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIrOTk4OTAzNDExNTExIiwiaWF0IjoxNjU0MDY3MzU5LCJleHAiOjE2NTY2NTkzNTksInJvbGVzIjpbeyJpZCI6Ijg0OGQxNjkwLWIzNDUtNDgxZC1iMDBiLTY0YmNjYTM2NzVhYiIsImNyZWF0ZWRBdCI6MTY1NDA0MTUyNDg4NCwidXBkYXRlZEF0IjoxNjU0MDQxNTI0ODg0LCJjcmVhdGVkQnkiOm51bGwsInVwZGF0ZWRCeSI6bnVsbCwibmFtZSI6IlVzZXIiLCJkZXNjcmlwdGlvbiI6IlVzZXIifV19.wk4mhvMv6gW-SOrbt1wcl6jUOldZPZeKo2e-S3BanoCm8yXCq8V9ukaOb7MTh_qroCNGvAq_dvu80YXu-8G9Gg")
    @POST("stadium/viewNearStadiums")
    suspend fun getNearByStadiums(@Body location: Location): StadiumResponse

    @GET("favorites/{userId}")
    suspend fun getFavouriteStadiums(@Path("userId") userId: String): StadiumResponse

}