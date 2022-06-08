package com.footzone.footzone.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.Location
import com.footzone.footzone.model.Response
import com.footzone.footzone.model.StadiumResponse
import com.footzone.footzone.model.playhistory.PlayHistoryResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _nearByStadiums =
        MutableStateFlow<UiStateObject<StadiumResponse>>(UiStateObject.EMPTY)
    val nearByStadiums = _nearByStadiums

    private val _favouriteStadiums =
        MutableStateFlow<UiStateObject<StadiumResponse>>(UiStateObject.EMPTY)
    val favouriteStadiums = _favouriteStadiums

    private val _previouslyBookedStadiums =
        MutableStateFlow<UiStateObject<PlayHistoryResponse>>(UiStateObject.EMPTY)
    val previouslyBookedStadiums = _previouslyBookedStadiums

    private val _addToFavouriteStadiums =
        MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val addToFavouriteStadiums = _addToFavouriteStadiums

    fun getNearByStadiums(location: Location) = viewModelScope.launch {
        _nearByStadiums.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getNearByStadiums(location)
            _nearByStadiums.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _nearByStadiums.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun getFavouriteStadiums(userId: String) = viewModelScope.launch {
        _favouriteStadiums.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getFavouriteStadiums(userId)
            _favouriteStadiums.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _favouriteStadiums.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun getPreviouslyBookedStadiums(userId: String) = viewModelScope.launch {
        _previouslyBookedStadiums.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getUserPlayHistory(userId)
            _previouslyBookedStadiums.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _previouslyBookedStadiums.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun addToFavouriteStadiums(stadiumId: String) = viewModelScope.launch {
        _addToFavouriteStadiums.value = UiStateObject.LOADING

        try {
            val response = mainRepository.addToFavouriteStadiums(stadiumId)
            _addToFavouriteStadiums.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _addToFavouriteStadiums.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}