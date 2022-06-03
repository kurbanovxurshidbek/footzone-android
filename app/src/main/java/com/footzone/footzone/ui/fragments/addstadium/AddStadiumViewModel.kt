package com.footzone.footzone.ui.fragments.addstadium

import androidx.lifecycle.ViewModel
import com.footzone.footzone.repository.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddStadiumViewModel  @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {
}