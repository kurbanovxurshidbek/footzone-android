package com.footzone.footzone.ui.fragments.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.Response
import com.footzone.footzone.model.profile.UserData
import com.footzone.footzone.repository.auth.AuthRepository
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _userData = MutableStateFlow<UiStateObject<UserData>>(UiStateObject.EMPTY)
    val userData = _userData

    private val _userProfile = MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val userProfile = _userProfile

    fun getUserData(userId: String) = viewModelScope.launch {
        _userData.value = UiStateObject.LOADING

        try {
            val response = mainRepository.getUserData(userId)
            _userData.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _userData.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun updateUserProfilePhoto(userId: String, file: MultipartBody.Part) = viewModelScope.launch {
        _userProfile.value = UiStateObject.LOADING

        try {
            val response = mainRepository.updateUserProfilePhoto(userId, file)
            _userProfile.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _userProfile.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}