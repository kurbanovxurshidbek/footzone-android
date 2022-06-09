package com.footzone.footzone.repository.main

import com.footzone.footzone.database.dao.FavouriteStadiumDao
import com.footzone.footzone.model.FavouriteStadium
import com.footzone.footzone.model.FavouriteStadiumRequest
import com.footzone.footzone.model.Location
import com.footzone.footzone.model.addstadium.Stadium
import com.footzone.footzone.networking.service.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService,
    private val favouriteStadiumDao: FavouriteStadiumDao
) {

    suspend fun getUserData(userId: String) = apiService.getUserData(userId)

    suspend fun getUserPlayHistory(userId: String) = apiService.getUserPlayHistory(userId)

    suspend fun updateUserProfilePhoto(userId: String, file: MultipartBody.Part) =
        apiService.updateUserProfilePhoto(userId, file)

    suspend fun getNearByStadiums(location: Location) =
        apiService.getNearByStadiums(location)

    suspend fun addToFavouriteStadiums(favouriteStadiumRequest: FavouriteStadiumRequest) =
        apiService.addToFavouriteStadiums(favouriteStadiumRequest)

    suspend fun getFavouriteStadiums(userId: String) = apiService.getFavouriteStadiums(userId)

    suspend fun getFavouriteStadiumsDB() = favouriteStadiumDao.getFavouriteStadiumsDB()

    suspend fun addToFavouriteStadiumsDB(favouriteStadium: FavouriteStadium) =
        favouriteStadiumDao.addToFavouriteStadiums(favouriteStadium)

    suspend fun getHolderStadiums(userId: String) = apiService.getHolderStadiums(userId)

    suspend fun getPitchData(stadiumId: String) = apiService.getPitchData(stadiumId)

   // suspend fun postHolderStadium(body: RequestBody) = apiService.postHolderStadium(body)
    suspend fun postHolderStadium(stadium: Stadium, files: ArrayList<MultipartBody.Part>) = apiService.postHolderStadium(stadium, files)

    suspend fun getHolderStadium(stadiumId: String) = apiService.getHolderStadium(stadiumId)
}