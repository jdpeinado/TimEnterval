package com.josedo.timenterval.utils

import android.widget.EditText

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
    }
}