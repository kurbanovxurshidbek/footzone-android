package com.footzone.footzone.ui.fragments.mystadium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.holders.HolderStadiumResponse
import com.footzone.footzone.model.playhistory.PlayHistoryResponse
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MyStadiumViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _holderStadiums = MutableStateFlow<UiStateObject<HolderStadiumResponse>>(UiStateObject.EMPTY)
    val holderStadiums = _holderStadiums

    fun getHolderStadiums(userId: String) = viewModelScope.launch {
        _holderStadiums.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getHolderStadiums(userId)
            _holderStadiums.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _holderStadiums.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

}