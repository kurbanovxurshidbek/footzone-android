package com.footzone.footzone.ui.fragments.addstadium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.*
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONArray
import javax.inject.Inject


@HiltViewModel
class AddStadiumViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _postStadium = MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val postStadium = _postStadium

    private val _getHolderStadium = MutableStateFlow<UiStateObject<FullStadiumDetailResponse>>(UiStateObject.EMPTY)
    val getHolderStadium = _getHolderStadium

    private val _editHolderStadium = MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val editHolderStadium = _editHolderStadium

    private val _deleteStadiumPhoto = MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val deleteStadiumPhoto = _deleteStadiumPhoto

    private val _addPhotoToStadium = MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val addPhotoToStadium = _addPhotoToStadium

    fun postHolderStadium(stadium: AddStadiumRequest, files: ArrayList<MultipartBody.Part>) = viewModelScope.launch {
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

    fun editHolderStadium(stadiumId: String, stadium: AddStadiumRequest) = viewModelScope.launch {
        _editHolderStadium.value = UiStateObject.LOADING

        try {
            val response = mainRepository.editHolderStadium(stadiumId, stadium)
            _editHolderStadium.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _editHolderStadium.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun deleteStadiumPhoto(stadiumId: String, photoId: String) = viewModelScope.launch {
        _deleteStadiumPhoto.value = UiStateObject.LOADING

        try {
            val response = mainRepository.deleteStadiumPhoto(stadiumId, photoId)
            _deleteStadiumPhoto.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _deleteStadiumPhoto.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun addPhotoToStadium(stadiumId: String, file: MultipartBody.Part) = viewModelScope.launch {
        _addPhotoToStadium.value = UiStateObject.LOADING

        try {
            val response = mainRepository.addPhotoToStadium(stadiumId, file)
            _addPhotoToStadium.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _addPhotoToStadium.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}