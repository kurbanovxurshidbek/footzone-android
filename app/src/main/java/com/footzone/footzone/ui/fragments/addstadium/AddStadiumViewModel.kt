package com.footzone.footzone.ui.fragments.addstadium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.addstadium.Stadium
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

    fun postHolderStadium(body: RequestBody) = viewModelScope.launch {
        _postStadium.value = UiStateObject.LOADING

        try {
            val response = mainRepository.postHolderStadium(body)
            _postStadium.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _postStadium.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

}