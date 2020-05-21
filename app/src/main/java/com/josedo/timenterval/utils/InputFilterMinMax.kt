package com.josedo.timenterval.utils

import android.text.InputFilter
import android.text.Spanned




class InputFilterMinMax(min:Int, max:Int): InputFilter {
    private var min:Int = 0
    private var max:Int = 0

    init{
        this.min = min
        this.max = max
    }

    override fun filter(source:CharSequence, start:Int, end:Int, dest: Spanned, dstart:Int, dend:Int): CharSequence? {
        try {
            val input = (dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length))
            if(input.indexOf(":")==-1 || input.indexOf(":") ==-1)
                return null
            val h = input.subSequence(0,input.indexOf(":"))
            val m = input.subSequence(input.indexOf(":")+1, input.length)
            if (h.length==2 && m.length==2){
               if (isInRange(min, max, h.toString().toInt()) && isInRange(min, max, m.toString().toInt()))
                   return null
            }
            else
                return null
        }
        catch (nfe:NumberFormatException) {}
        return ""
    }

    private fun isInRange(a:Int, b:Int, c:Int):Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}