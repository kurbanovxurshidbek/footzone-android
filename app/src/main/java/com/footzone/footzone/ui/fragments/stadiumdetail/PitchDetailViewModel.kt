package com.footzone.footzone.ui.fragments.stadiumdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.FullStadiumDetailResponse
import com.footzone.footzone.model.Response
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateList
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PitchDetailViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
    private val _pitchData = MutableStateFlow<UiStateObject<FullStadiumDetailResponse>>(UiStateObject.EMPTY)
    val pitchData = _pitchData

    private val _pitchComment = MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val pitchComment = _pitchComment

    fun getPitchData(stadiumId: String) = viewModelScope.launch {
        _pitchData.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getPitchData(stadiumId)
            _pitchData.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _pitchData.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun getCommentAllByStadiumId(stadiumId: String) = viewModelScope.launch {
        _pitchComment.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getCommentAllByStadiumId(stadiumId)
            _pitchComment.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _pitchComment.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }

    }
}