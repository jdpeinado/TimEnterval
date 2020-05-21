package com.josedo.timenterval.utils

import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.josedo.timenterval.view.ui.fragment.HomeFragment
import java.lang.reflect.Field
import java.util.*


class Utils {
    companion object{
        fun getMiliFromEt(editText: EditText): Long {
            try {
                val input = editText.text.toString()
                var m = 0
                var s = 0
                if (input.indexOf(":") != -1 || input.indexOf(":") != -1) {
                    m = input.subSequence(0, input.indexOf(":")).toString().toInt()
                    s = input.subSequence(input.indexOf(":") + 1, input.length).toString().toInt()
                }
                return ((s * 1000) + (m * 60 * 1000)).toLong()
            } catch (ex: NumberFormatException){
                return 0L
            }
        }

        fun getMinutesFromMiliseconds(mili: Long): Int{
            return (mili / 1000).toInt() / 60
        }

        fun getSecondsFromMiliseconds(mili: Long): Int{
            return (mili / 1000).toInt() % 60
        }

        fun addTime(editText: EditText) {
            val mili = Utils.getMiliFromEt(editText)
            var minutes = Utils.getMinutesFromMiliseconds(mili)
            var seconds = Utils.getSecondsFromMiliseconds(mili)
            if (minutes == HomeFragment.maxMinutes && seconds == HomeFragment.maxSeconds) {
                //maximum, do nothing
            } else {
                if (seconds == HomeFragment.maxSeconds) {
                    minutes += 1
                    seconds = 0
                    editText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds), TextView.BufferType.EDITABLE)
                } else {
                    seconds += 1
                    editText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds), TextView.BufferType.EDITABLE)
                }
            }
        }

        fun removeTime(editText: EditText) {
            val mili = Utils.getMiliFromEt(editText)
            var minutes = Utils.getMinutesFromMiliseconds(mili)
            var seconds = Utils.getSecondsFromMiliseconds(mili)
            if (minutes == 0 && seconds == 0) {
                //minimum, do nothing
            } else {
                if (seconds == 0) {
                    minutes -= 1
                    seconds = HomeFragment.maxSeconds
                    editText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds), TextView.BufferType.EDITABLE)
                } else {
                    seconds -= 1
                    editText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds), TextView.BufferType.EDITABLE)
                }
            }
        }
    }
}