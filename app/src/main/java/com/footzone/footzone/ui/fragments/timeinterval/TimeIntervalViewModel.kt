package com.footzone.footzone.ui.fragments.timeinterval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.sessionsday.SessionsDayResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class TimeIntervalViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
    private val _sessionsDay = MutableStateFlow<UiStateObject<SessionsDayResponse>>(UiStateObject.EMPTY)
    val sessionsDay = _sessionsDay

    fun getSessionsForSpecificDay(stadiumId: String, date: String) = viewModelScope.launch {
        _sessionsDay.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getSessionsForSpecificDay(stadiumId, date)
            _sessionsDay.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _sessionsDay.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}