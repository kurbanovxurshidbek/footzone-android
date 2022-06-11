package com.footzone.footzone.networking.service

import com.footzone.footzone.model.*
import com.footzone.footzone.model.addstadium.Stadium
import com.footzone.footzone.model.holderpitch.HolderStadium
import com.footzone.footzone.model.holderpitchs.HolderPitches
import com.footzone.footzone.model.playhistory.PlayHistoryResponse
import com.footzone.footzone.model.profile.UserData
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
    suspend fun registerUser(@Body user: User): RegisterResponse

    @POST("stadium/viewNearStadiums")
    suspend fun getNearByStadiums(@Body location: Location): NearStadiumResponse

    @GET("favorites/{userId}")
    suspend fun getFavouriteStadiums(@Path("userId") userId: String): StadiumResponse

    @POST("favorites")
    suspend fun addToFavouriteStadiums(@Body favouriteStadiumRequest: FavouriteStadiumRequest): Response

    @GET("user/{userId}")
    suspend fun getUserData(@Path("userId") userId: String): UserData

    @GET("stadium/history/{userId}")
    suspend fun getUserPlayHistory(@Path("userId") userId: String): StadiumResponse

    @Multipart
    @POST("user/changeProfilePicture/{userId}")
    suspend fun updateUserProfilePhoto(
        @Path("userId") userId: String,
        @Part file: MultipartBody.Part,
    ): Response


    //not yet fully connected
    @GET("stadium/{stadiumId}")
    suspend fun getPitchData(@Path("stadiumId") stadiumId: String): Response

    @GET("stadium/holder/{userId}")
    suspend fun getHolderStadiums(@Path("userId") userId: String): HolderPitches

    //the stadium owner adds the stadium
    @Multipart
    @POST("stadium")
    suspend fun postHolderStadium(
        @Part("stadium") stadium: Stadium,
        @Part files: List<MultipartBody.Part>,
    ): String

    @GET("stadium/{stadiumId}")
    suspend fun getHolderStadium(@Path("stadiumId") stadiumId: String): HolderStadium

    @GET("stadium/all")
    suspend fun getAllStadiums(): AllStadiumResponse

    @GET("stadium/all?")
    suspend fun getSearchedStadiums(
        @Query("search") search: String
    ): StadiumResponse
}