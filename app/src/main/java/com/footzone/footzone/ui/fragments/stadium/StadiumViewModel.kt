package com.footzone.footzone.ui.fragments.stadium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.CommentsData
import com.footzone.footzone.model.FullStadiumDetailResponse
import com.footzone.footzone.model.ShortStadiumDetailResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class StadiumViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _getHolderStadium = MutableStateFlow<UiStateObject<FullStadiumDetailResponse>>(
        UiStateObject.EMPTY)
    val getHolderStadium = _getHolderStadium

    private val _pitchComment = MutableStateFlow<UiStateObject<CommentsData>>(UiStateObject.EMPTY)
    val pitchComment = _pitchComment

    fun getHolderStadiums(stadiumId: String) = viewModelScope.launch {
        _getHolderStadium.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getHolderStadium(stadiumId)
            _getHolderStadium.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _getHolderStadium.value =
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