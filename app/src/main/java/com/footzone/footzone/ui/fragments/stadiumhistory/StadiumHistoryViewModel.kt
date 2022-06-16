package com.footzone.footzone.ui.fragments.stadiumhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.StadiumBookSentResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StadiumHistoryViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _stadiumPlayedHistory =
        MutableStateFlow<UiStateObject<StadiumBookSentResponse>>(UiStateObject.EMPTY)
    val stadiumPlayedHistory = _stadiumPlayedHistory

    fun getStadiumPlayedHistory(status: String) = viewModelScope.launch {
        _stadiumPlayedHistory.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getSentBookingRequests(status)
            _stadiumPlayedHistory.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _stadiumPlayedHistory.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

}