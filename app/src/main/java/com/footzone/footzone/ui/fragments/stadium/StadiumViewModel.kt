package com.footzone.footzone.ui.fragments.stadium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.holders.HolderStadiumResponse
import com.footzone.footzone.model.holderstadium.HolderStadium
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

    private val _getHolderStadium = MutableStateFlow<UiStateObject<HolderStadium>>(
        UiStateObject.EMPTY)
    val getHolderStadium = _getHolderStadium

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


}