package com.josedo.timenterval.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.josedo.timenterval.model.IntervalTime

class ShareViewModel(application: Application): AndroidViewModel(application)  {
    var intervalTime: MutableLiveData<IntervalTime>
    var sameTime: MutableLiveData<Boolean>
    var sameTimeRestRounds: MutableLiveData<Boolean>
    var sameTimeSeries: MutableLiveData<Boolean>
    var sameTimeRestSeries: MutableLiveData<Boolean>
    var soundIsPlaying: MutableLiveData<Boolean>
    var mTimerRunning: MutableLiveData<Boolean>

    init {
        intervalTime = MutableLiveData<IntervalTime>(IntervalTime())
        sameTime = MutableLiveData<Boolean>(true)
        sameTimeRestRounds = MutableLiveData<Boolean>(false)
        sameTimeSeries = MutableLiveData<Boolean>(false)
        sameTimeRestSeries = MutableLiveData<Boolean>(false)
        soundIsPlaying = MutableLiveData<Boolean>(false)
        mTimerRunning = MutableLiveData<Boolean>(false)
    }

    fun refresh(){
        intervalTime = MutableLiveData<IntervalTime>(IntervalTime())
    }

}