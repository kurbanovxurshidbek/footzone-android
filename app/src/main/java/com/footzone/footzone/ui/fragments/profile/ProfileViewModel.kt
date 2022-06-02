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
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _userData = MutableStateFlow<UiStateObject<UserData>>(UiStateObject.EMPTY)
    val userData = _userData

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
}