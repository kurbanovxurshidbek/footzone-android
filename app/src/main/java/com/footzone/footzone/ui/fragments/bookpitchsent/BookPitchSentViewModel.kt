package com.footzone.footzone.ui.fragments.bookpitchsent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.AcceptDeclineRequest
import com.footzone.footzone.model.AllStadiumResponse
import com.footzone.footzone.model.Response
import com.footzone.footzone.model.StadiumBookSentResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateList
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class BookPitchSentViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
    private val _acceptDecline =
        MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val acceptDecline = _acceptDecline

    private val _bookRequests =
        MutableStateFlow<UiStateObject<StadiumBookSentResponse>>(UiStateObject.EMPTY)
    val bookRequests = _bookRequests

    fun acceptOrDeclineBookingRequest(acceptDeclineRequest: AcceptDeclineRequest) =
        viewModelScope.launch {
            _acceptDecline.value = UiStateObject.LOADING

            try {
                val response = mainRepository.acceptOrDeclineBookingRequest(acceptDeclineRequest)
                _acceptDecline.value = UiStateObject.SUCCESS(response)

            } catch (e: Exception) {
                _acceptDecline.value =
                    UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
            }
        }

    fun getSentBookingRequests(status: String) =
        viewModelScope.launch {
            _bookRequests.value = UiStateObject.LOADING

            try {
                val response = mainRepository.getSentBookingRequests(status)
                _bookRequests.value = UiStateObject.SUCCESS(response)
            } catch (e: Exception) {
                _bookRequests.value =
                    UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
            }
        }
}