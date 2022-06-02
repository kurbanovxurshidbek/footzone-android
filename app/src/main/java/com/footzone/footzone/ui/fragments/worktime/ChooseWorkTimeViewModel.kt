package com.footzone.footzone.ui.fragments.worktime

import androidx.lifecycle.ViewModel
import com.footzone.footzone.repository.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseWorkTimeViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
}