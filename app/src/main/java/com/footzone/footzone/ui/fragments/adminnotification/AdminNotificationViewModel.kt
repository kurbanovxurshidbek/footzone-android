package com.footzone.footzone.ui.fragments.adminnotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.AcceptDeclineRequest
import com.footzone.footzone.model.Response
import com.footzone.footzone.model.StadiumBookSentResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminNotificationViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _adminNotification =
        MutableStateFlow<UiStateObject<StadiumBookSentResponse>>(UiStateObject.EMPTY)
    val adminNotification = _adminNotification

    private val _acceptDecline =
        MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val acceptDecline = _acceptDecline

    fun getAllNotifications(status: String) =
        viewModelScope.launch {
            _adminNotification.value = UiStateObject.LOADING

            try {
                val response = mainRepository.getSentBookingRequests(status)
                _adminNotification.value = UiStateObject.SUCCESS(response)
            } catch (e: Exception) {
                _adminNotification.value =
                    UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
            }
        }

    fun acceptOrDeclineBookingRequest(acceptDeclineRequest: AcceptDeclineRequest) =
        viewModelScope.launch {
            _acceptDecline.value = UiStateObject.LOADING

            try {
                val response = mainRepository.acceptOrDeclineBookingRequest(acceptDeclineRequest)
                _acceptDecline.value = UiStateObject.SUCCESS(response)

            } catch (e: java.lang.Exception) {
                _acceptDecline.value =
                    UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
            }
        }

}