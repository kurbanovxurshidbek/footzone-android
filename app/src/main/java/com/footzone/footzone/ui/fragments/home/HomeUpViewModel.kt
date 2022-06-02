package com.footzone.footzone.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.model.User
import com.footzone.footzone.repository.auth.AuthRepository
import com.footzone.footzone.repository.main.MainRepository
import com.footzone.footzone.utils.UiStateList
import com.footzone.footzone.utils.UiStateObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeUpViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
    private val _userState = MutableStateFlow<UiStateList<Pitch>>(UiStateList.EMPTY)
    val user = _userState


}