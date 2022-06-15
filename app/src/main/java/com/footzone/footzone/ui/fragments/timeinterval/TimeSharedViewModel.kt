package com.footzone.footzone.ui.fragments.timeinterval

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.footzone.footzone.model.LiveDataModel

class TimeSharedViewModel : ViewModel() {

    private val timeState = MutableLiveData<LiveDataModel>()

    fun setTime(time: LiveDataModel) {
        timeState.value = time
    }

    fun getTime(): LiveData<LiveDataModel?> {
        return timeState
    }
}