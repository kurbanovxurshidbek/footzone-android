package com.footzone.footzone.ui.fragments.addstadium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.addstadium.Stadium
import com.footzone.footzone.model.holderstadium.HolderStadium
import com.footzone.footzone.model.profile.UserData
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AddStadiumViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _postStadium = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val postStadium = _postStadium
    private val _getHolderStadium = MutableStateFlow<UiStateObject<HolderStadium>>(
        UiStateObject.EMPTY)
    val getHolderStadium = _getHolderStadium

    fun postHolderStadium(stadium: Stadium, files: ArrayList<MultipartBody.Part>) = viewModelScope.launch {
        _postStadium.value = UiStateObject.LOADING

        try {
            val response = mainRepository.postHolderStadium(stadium, files)
            _postStadium.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _postStadium.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

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