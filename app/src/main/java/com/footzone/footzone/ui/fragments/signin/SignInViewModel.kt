package com.footzone.footzone.ui.fragments.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.Response
import com.footzone.footzone.model.User
import com.footzone.footzone.repository.auth.AuthRepository
import com.footzone.footzone.utils.UiStateList
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {
    private val _userPhoneNumber = MutableStateFlow<UiStateObject<Response>>(UiStateObject.EMPTY)
    val userPhoneNumber = _userPhoneNumber

    fun signIn(phoneNumber: String) = viewModelScope.launch {
        _userPhoneNumber.value = UiStateObject.LOADING

        try {
            val response = authRepository.signIn(phoneNumber)
            _userPhoneNumber.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _userPhoneNumber.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}