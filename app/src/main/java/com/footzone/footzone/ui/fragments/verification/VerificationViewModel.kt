package com.footzone.footzone.ui.fragments.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.*
import com.footzone.footzone.repository.auth.AuthRepository
import com.footzone.footzone.utils.UiStateList
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {
    private val _smsVerification =
        MutableStateFlow<UiStateObject<SmsVerificationResponse>>(UiStateObject.EMPTY)
    val smsVerification = _smsVerification

    private val _registerUser =
        MutableStateFlow<UiStateObject<RegisterResponse>>(UiStateObject.EMPTY)
    val registerUser = _registerUser

    private val _signInVerification =
        MutableStateFlow<UiStateObject<LogInResponse>>(UiStateObject.EMPTY)
    val signInVerification = _signInVerification

    fun signUp(smsVerification: SmsVerification) = viewModelScope.launch {
        _smsVerification.value = UiStateObject.LOADING

        try {
            val response = authRepository.checkValidation(smsVerification)
            _smsVerification.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _smsVerification.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun signIn(signInVerification: SignInVerification) = viewModelScope.launch {
        _signInVerification.value = UiStateObject.LOADING

        try {
            val response = authRepository.signInVerification(signInVerification)
            _signInVerification.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _signInVerification.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun registerUser(user: User) = viewModelScope.launch {
        _registerUser.value = UiStateObject.LOADING

        try {
            val response = authRepository.registerUser(user)
            _registerUser.value = UiStateObject.SUCCESS(response)

        } catch (e: Exception) {
            _registerUser.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}