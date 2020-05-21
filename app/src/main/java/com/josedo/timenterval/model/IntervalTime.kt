package com.josedo.timenterval.model

import android.util.Log

class IntervalTime {
    var roundsNumber: Int = 0
    var seriesNumber: Int = 0
    var restTimeRounds: MutableList<Long> = arrayListOf()
    var seriesTime: MutableList<Long> = arrayListOf()
    var actualRound: Int = 0
    var actualSerie: Int = 0
    var isRestRound: Boolean = false
    var mTimeLeftInMillis: Long = 0
    var isPreparing: Boolean = false
    var prepareTime: Long = 0

    fun isEnd(): Boolean {
        if ((actualRound + 1) == roundsNumber && (actualSerie + 1) == seriesNumber) {
            return true
        }
        return false
    }

    fun setNextTime() {
        if (isPreparing) {
            isPreparing = false
            mTimeLeftInMillis = seriesTime[actualSerie]
        } else if (isEnd()) {
            mTimeLeftInMillis = 0
        } else {
            if (actualSerie + 1 == seriesNumber) {
                if (isRestRound) {
                    actualSerie = 0
                    actualRound += 1
                    isRestRound = false
                    mTimeLeftInMillis = seriesTime[actualSerie]
                } else {
                    isRestRound = true
                    mTimeLeftInMillis = restTimeRounds[actualRound]
                }
            } else {
                Log.d("IntervalTime", "oldAactualSerie is: $actualSerie")
                actualSerie += 1
                Log.d("IntervalTime", "actualSerie is: $actualSerie")
                mTimeLeftInMillis = seriesTime[actualSerie]
            }
        }
    }

    private fun setFirstTime() {
        if (roundsNumber > 0 && seriesNumber > 0 && seriesTime.size > 0 && seriesTime[0] != 0L) {
            mTimeLeftInMillis = prepareTime
        }
    }

    fun isReady(): Boolean {
        if (roundsNumber > 0 && seriesNumber > 0 && seriesTime.size > 0 && seriesTime[0] != 0L) {
            return true
        }
        return false
    }

    fun reset() {
        isPreparing = true
        actualRound = 0
        actualSerie = 0
        setFirstTime()
    }

    fun setRounds(rounds: Int) {
        roundsNumber = rounds
        val restTimeRoundsCopy = restTimeRounds.toMutableList()
        restTimeRounds = MutableList<Long>(rounds) { 0 }
        for ((index, i) in restTimeRoundsCopy.withIndex()) {
            if (index >= rounds) {
                break
            } else {
                restTimeRounds!![index] = i
            }
        }
    }

    fun setSeries(series: Int) {
        seriesNumber = series
        val seriesTimeCopy = seriesTime.toMutableList()
        seriesTime = MutableList<Long>(series) { 0 }
        for ((index, i) in seriesTimeCopy.withIndex()) {
            if (index >= series) {
                break
            } else {
                seriesTime!![index] = i
            }
        }
    }

    fun updateSeries(sameTime: Boolean, mili: Long, isRest: Boolean) {
        if (sameTime) {
            restTimeRounds = MutableList<Long>(roundsNumber) { 0 }
            if (seriesTime?.size == 0) {
                seriesTime = MutableList<Long>(seriesNumber) { 0 }
                if (isRest) {
                    for (i in 1 until seriesNumber step 2) { //rest time
                        seriesTime!![i] = mili
                    }
                } else {
                    for (i in 0 until seriesNumber step 2) { //rest time
                        seriesTime!![i] = mili
                    }
                }
            } else {
                val seriesTimeCopy = seriesTime.toMutableList()
                seriesTime = MutableList<Long>(seriesNumber) { 0 }
                for ((index, i) in seriesTimeCopy.withIndex()) {
                    if (index >= seriesNumber) {
                        break
                    } else {
                        if (isRest && index.rem(2) != 0)
                            seriesTime[index] = mili
                        else if (!isRest && index.rem(2) == 0)
                            seriesTime[index] = mili
                        else
                            seriesTime[index] = i
                    }
                }
            }
        } else {
            if (restTimeRounds.size == 0) {
                restTimeRounds = MutableList<Long>(roundsNumber) { 0 }
            } else {
                val restTimeRoundsCopy = restTimeRounds.toMutableList()
                restTimeRounds = MutableList<Long>(roundsNumber) { 0 }
                for ((index, i) in restTimeRoundsCopy.withIndex()) {
                    if (index >= roundsNumber) {
                        break
                    } else {
                        restTimeRounds[index] = i
                    }
                }
            }
            if (seriesTime.size == 0) {
                seriesTime = MutableList<Long>(seriesNumber) { 0 }
            } else {
                val seriesTimeCopy = seriesTime.toMutableList()
                seriesTime = MutableList<Long>(seriesNumber) { 0 }
                for ((index, i) in seriesTimeCopy.withIndex()) {
                    if (index >= seriesNumber) {
                        break
                    } else {
                        seriesTime[index] = i
                    }
                }
            }
        }
        //reset()
    }

    fun setPreparedTime(mili: Long) {
        prepareTime = mili
        reset()
    }
}