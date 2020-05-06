package com.josedo.timenterval.model

import android.util.Log

class IntervalTime {
    var roundsNumber: Int = 3
    var seriesNumber: Int = 6
    var restTimeRounds: List<Long> = arrayListOf(5000, 5000, 0)
    var seriesTime: List<Long> = arrayListOf(8000, 5000, 8000, 5000, 8000, 0)
    var actualRound: Int = 0
    var actualSerie: Int = 0
    var isRestRound: Boolean = false
    var mTimeLeftInMillis: Long = seriesTime[actualSerie]

    fun isEnd(): Boolean {
        if((actualRound+1) == roundsNumber && (actualSerie+1) == seriesNumber) {
            return true
        }
        return false
    }

    fun setNextTime(){
        if(isEnd()) {
            mTimeLeftInMillis = 0
        }else {
            if (actualSerie + 1 == seriesNumber) {
                if(isRestRound) {
                    actualSerie = 0
                    actualRound = actualRound+1
                    isRestRound = false
                    mTimeLeftInMillis = seriesTime[actualSerie]
                } else {
                    isRestRound = true
                    mTimeLeftInMillis = restTimeRounds[actualRound]
                }
            } else {
                Log.d("IntervalTime", "oldAactualSerie is: $actualSerie")
                actualSerie=actualSerie+1
                Log.d("IntervalTime", "actualSerie is: $actualSerie")
                mTimeLeftInMillis = seriesTime[actualSerie]
            }
        }
    }
}