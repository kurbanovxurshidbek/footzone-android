package com.footzone.footzone.ui.fragments.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.playhistory.PlayHistoryResponse
import com.footzone.footzone.model.profile.UserData
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class TableViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
    private val _playHistory = MutableStateFlow<UiStateObject<PlayHistoryResponse>>(UiStateObject.EMPTY)
    val playHistory = _playHistory

    fun getUserPlayHistory(userId: String) = viewModelScope.launch {
        _playHistory.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getUserPlayHistory(userId)
            _playHistory.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _playHistory.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

}