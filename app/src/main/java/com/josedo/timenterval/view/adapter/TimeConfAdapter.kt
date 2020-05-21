package com.josedo.timenterval.view.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josedo.timenterval.R
import com.josedo.timenterval.utils.Utils
import kotlinx.android.synthetic.main.time_picker_dialog.*
import java.util.*
import kotlin.collections.ArrayList

class TimeConfAdapter(val context: Context, val timeConfListener: TimeConfListener): RecyclerView.Adapter<TimeConfAdapter.ViewHolder>() {

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

        showTimerPickerDialog(holder.etSerieOrRoundTime, position)

        holder.addRoundOrSerie.setOnClickListener {
            Utils.addTime(holder.etSerieOrRoundTime)
            timeConfListener.onUpdateDataListener(position, holder.etSerieOrRoundTime)
        }

        holder.removeRoundOrSerie.setOnClickListener {
            Utils.removeTime(holder.etSerieOrRoundTime)
            timeConfListener.onUpdateDataListener(position, holder.etSerieOrRoundTime)
        }
    }

    fun updateData(data: List<HashMap<String,Long>>){
        listItems.clear()
        listItems.addAll(data)
        notifyDataSetChanged()
    }

    private fun showTimerPickerDialog(editText: EditText, position: Int){
        editText.setOnClickListener {
            val mDialogView = LayoutInflater.from(ContextThemeWrapper(context, R.style.NumberPickerTextColorStyle)).inflate(R.layout.time_picker_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.setCanceledOnTouchOutside(true)
            mAlertDialog.bAcceptPicker.setOnClickListener {
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", mAlertDialog.minutes.value, mAlertDialog.seconds.value), TextView.BufferType.EDITABLE)
                timeConfListener.onUpdateDataListener(position, editText)
                mAlertDialog.dismiss()
            }
            mAlertDialog.seconds.maxValue = 59
            mAlertDialog.seconds.minValue = 0
            mAlertDialog.minutes.maxValue = 99
            mAlertDialog.minutes.minValue = 0
            val mili = Utils.getMiliFromEt(editText)
            mAlertDialog.minutes.setValue(Utils.getMinutesFromMiliseconds(mili))
            mAlertDialog.seconds.setValue(Utils.getSecondsFromMiliseconds(mili))
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSerieOrRound = itemView.findViewById<TextView>(R.id.tvSerieOrRound1)
        val etSerieOrRoundTime = itemView.findViewById<EditText>(R.id.etSerieOrRoundTime)
        val addRoundOrSerie = itemView.findViewById<ImageView>(R.id.addRoundOrSerie)
        val removeRoundOrSerie = itemView.findViewById<ImageView>(R.id.removeRoundOrSerie)
    }

}