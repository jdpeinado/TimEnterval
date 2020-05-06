package com.josedo.timenterval.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.josedo.timenterval.model.IntervalTime

class ShareViewModel(application: Application): AndroidViewModel(application)  {
    var intervalTime: MutableLiveData<IntervalTime>

    init {
        intervalTime = MutableLiveData<IntervalTime>(IntervalTime())
    }

    fun refresh(){
        intervalTime = MutableLiveData<IntervalTime>(IntervalTime())
    }
}