package com.footzone.footzone.repository.main

import com.footzone.footzone.model.AddStadiumRequest
import com.footzone.footzone.model.FavouriteStadiumResponse
import com.footzone.footzone.model.FavouriteStadiumRequest
import com.footzone.footzone.model.Location
import com.footzone.footzone.database.dao.FavouriteStadiumDao
import com.footzone.footzone.model.*
import com.footzone.footzone.networking.service.ApiService
import okhttp3.MultipartBody
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService
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

    suspend fun getFavouriteStadiumsList(userId: String) =
        apiService.getFavouriteStadiumsList(userId)

    suspend fun getHolderStadiums(userId: String) = apiService.getHolderStadiums(userId)

    suspend fun getPitchData(stadiumId: String) = apiService.getPitchData(stadiumId)

    suspend fun postHolderStadium(
        stadium: AddStadiumRequest,
        files: ArrayList<MultipartBody.Part>
    ) =
        apiService.postHolderStadium(stadium, files)

    suspend fun getHolderStadium(stadiumId: String) = apiService.getHolderStadium(stadiumId)

    suspend fun getAllStadiums() =
        apiService.getAllStadiums()

    suspend fun getSearchedStadiums(search: String) =
        apiService.getSearchedStadiums(search)

    suspend fun editHolderStadium(
        stadiumId: String,
        stadium: AddStadiumRequest) =
        apiService.editHolderStadium(stadiumId, stadium)

    suspend fun editHolderStadiumPhoto(stadiumId: String, files: ArrayList<EditStadiumPhotoRequest>) = apiService.editHolderStadiumPhoto(stadiumId, files)
}