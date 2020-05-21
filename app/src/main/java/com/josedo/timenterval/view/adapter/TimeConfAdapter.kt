package com.josedo.timenterval.view.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josedo.timenterval.R
import com.josedo.timenterval.utils.InputFilterMinMax
import kotlinx.android.synthetic.main.item_time_conf.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TimeConfAdapter(val timeConfListener: TimeConfListener): RecyclerView.Adapter<TimeConfAdapter.ViewHolder>() {

    var listItems = ArrayList<HashMap<String,Long>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeConfAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_time_conf, parent, false))
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = listItems[position] as HashMap<String, Long>

        holder.tvSerieOrRound.text = map.keys.elementAt(0)

        val timeInMili = map[map.keys.elementAt(0)]
        val seconds = timeInMili?.div(1000)?.rem(60)
        val minutes = timeInMili?.div(1000)?.div(60)
        holder.etSerieOrRoundTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds), TextView.BufferType.EDITABLE)

        holder.etSerieOrRoundTime.filters = arrayOf(InputFilterMinMax(0, 59))
        holder.etSerieOrRoundTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                timeConfListener.onUpdateDataListener(position, holder.etSerieOrRoundTime.text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        holder.addRoundOrSerie.setOnClickListener {
            addTime(holder.etSerieOrRoundTime)
            timeConfListener.onUpdateDataListener(position, holder.etSerieOrRoundTime.text.toString())
        }

        holder.removeRoundOrSerie.setOnClickListener {
            removeTime(holder.etSerieOrRoundTime)
            timeConfListener.onUpdateDataListener(position, holder.etSerieOrRoundTime.text.toString())
        }
    }

    private fun addTime(editText: EditText) {
        val input = editText.text.toString()
        if (input.indexOf(":") == -1 || input.indexOf(":") == -1)
            editText.setText("00:01", TextView.BufferType.EDITABLE)
        val h = input.subSequence(0, input.indexOf(":")).toString()
        val m = input.subSequence(input.indexOf(":") + 1, input.length).toString()
        var h_int = h.toInt()
        var m_int = m.toInt()
        if (h_int == 59 && m_int == 59) {
            //maximum, do nothing
        } else {
            if (m_int == 59) {
                h_int += 1
                m_int = 0
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", h_int, m_int), TextView.BufferType.EDITABLE)
            } else {
                m_int += 1
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", h_int, m_int), TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun removeTime(editText: EditText) {
        val input = editText.text.toString()
        if (input.indexOf(":") == -1 || input.indexOf(":") == -1)
            editText.setText("00:01", TextView.BufferType.EDITABLE)
        val h = input.subSequence(0, input.indexOf(":")).toString()
        val m = input.subSequence(input.indexOf(":") + 1, input.length).toString()
        var h_int = h.toInt()
        var m_int = m.toInt()
        if (h_int == 0 && m_int == 0) {
            //minimum, do nothing
        } else {
            if (m_int == 0) {
                h_int -= 1
                m_int = 59
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", h_int, m_int), TextView.BufferType.EDITABLE)
            } else {
                m_int -= 1
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", h_int, m_int), TextView.BufferType.EDITABLE)
            }
        }
    }

    fun updateData(data: List<HashMap<String,Long>>){
        listItems.clear()
        listItems.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSerieOrRound = itemView.findViewById<TextView>(R.id.tvSerieOrRound1)
        val etSerieOrRoundTime = itemView.findViewById<EditText>(R.id.etSerieOrRoundTime)
        val addRoundOrSerie = itemView.findViewById<ImageView>(R.id.addRoundOrSerie)
        val removeRoundOrSerie = itemView.findViewById<ImageView>(R.id.removeRoundOrSerie)
    }

}