package com.footzone.footzone.ui.fragments.playing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.PlayedHistoryResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PlayingPitchViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _playingSoonStadiums =
        MutableStateFlow<UiStateObject<PlayedHistoryResponse>>(UiStateObject.EMPTY)
    val playingSoonStadiums = _playingSoonStadiums

    fun getPlayingSoonStadium() = viewModelScope.launch {
        _playingSoonStadiums.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getPlayingSoonStadium()
            _playingSoonStadiums.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _playingSoonStadiums.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}