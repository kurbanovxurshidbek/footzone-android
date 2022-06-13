package com.footzone.footzone.networking.service

import com.footzone.footzone.model.*
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
    suspend fun signInVerification(@Body signInVerification: SignInVerification): LogInResponse

    @POST("auth/register")
    suspend fun registerUser(@Body user: User): RegisterResponse

    @POST("stadium/viewNearStadiums")
    suspend fun getNearByStadiums(@Body location: Location): ShortStadiumDetailResponse

    @GET("favorites/{userId}")
    suspend fun getFavouriteStadiums(@Path("userId") userId: String): ShortStadiumDetailResponse

    @POST("favorites")
    suspend fun addToFavouriteStadiums(@Body favouriteStadiumRequest: FavouriteStadiumRequest): Response

    @GET("favorites/list/{userId}")
    suspend fun getFavouriteStadiumsList(@Path("userId") userId: String): FavouriteStadiumResponse

    @GET("user/{userId}")
    suspend fun getUserData(@Path("userId") userId: String): UserData

    @GET("stadium/history/{userId}")
    suspend fun getUserPlayHistory(@Path("userId") userId: String): ShortStadiumDetailResponse

    @Multipart
    @POST("user/changeProfilePicture/{userId}")
    suspend fun updateUserProfilePhoto(
        @Path("userId") userId: String,
        @Part file: MultipartBody.Part,
    ): Response


    //not yet fully connected
    @GET("stadium/{stadiumId}")
    suspend fun getPitchData(@Path("stadiumId") stadiumId: String): FullStadiumDetailResponse

    @GET("stadium/holder/{userId}")
    suspend fun getHolderStadiums(@Path("userId") userId: String): ShortStadiumDetailResponse

    //the stadium owner adds the stadium
    @Multipart
    @POST("stadium")
    suspend fun postHolderStadium(
        @Part("stadium") stadium: AddStadiumRequest,
        @Part files: List<MultipartBody.Part>,
    ): Response

    @GET("stadium/{stadiumId}")
    suspend fun getHolderStadium(@Path("stadiumId") stadiumId: String): FullStadiumDetailResponse

    @GET("stadium/all")
    suspend fun getAllStadiums(): AllStadiumResponse

    @GET("stadium/search?")
    suspend fun getSearchedStadiums(
        @Query("search") search: String
    ): ShortStadiumDetailResponse

    @PUT("stadium/edit/content/{stadiumId}")
    suspend fun editHolderStadium(
        @Path("stadiumId") stadiumId: String,
        @Body stadium: AddStadiumRequest
    ): Response

    @Multipart
    @PUT("stadium/edit/photo/{stadiumId}")
    suspend fun editHolderStadiumPhoto(
        @Path("stadiumId") stadiumId: String,
        @Part("files") files: ArrayList<EditStadiumPhotoRequest>
    ): Response

    @PUT("user/edit/{userId}")
    suspend fun editUser(
        @Path("userId") userId: String,
        @Body body: EditNameRequest
    ): Response

    @GET("comment/{stadiumId}")
    suspend fun getCommentAllByStadiumId(@Path("stadiumId") stadiumId: String): Response

    @POST("session")
    fun sendBookingRequest(): Response

    @PUT("session/{sessionId}")
    fun editSession(@Path("sessionId") sessionId: String)

    @POST("session/acceptOrDecline")
    fun acceptOrDeclineBookingRequest(acceptDeclineRequest: AcceptDeclineRequest): Response

    @GET("session/requests/{status}")
    fun getSentBookingRequests(@Path("status") status: String): Response
}