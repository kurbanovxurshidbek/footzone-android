package com.footzone.footzone.ui.fragments.played

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.AllStadiumResponse
import com.footzone.footzone.model.PlayedHistoryResponse
import com.footzone.footzone.model.Response
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PlayedPitchViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
    private val _playedStadiums =
        MutableStateFlow<UiStateObject<PlayedHistoryResponse>>(UiStateObject.EMPTY)
    val playedStadiums = _playedStadiums

    fun getPlayedHistory(userId: String) = viewModelScope.launch {
        _playedStadiums.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getPlayedHistory(userId)
            _playedStadiums.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _playedStadiums.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}