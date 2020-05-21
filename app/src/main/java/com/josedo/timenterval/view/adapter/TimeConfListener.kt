package com.josedo.timenterval.view.adapter

import android.widget.EditText

interface TimeConfListener {
    fun onUpdateDataListener(position: Int, editText: EditText)
}