package com.josedo.timenterval.view.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.josedo.timenterval.R
import com.josedo.timenterval.utils.Utils
import com.josedo.timenterval.view.adapter.TimeConfAdapter
import com.josedo.timenterval.view.adapter.TimeConfListener
import com.josedo.timenterval.viewmodel.ShareViewModel
import kotlinx.android.synthetic.main.fragment_time_conf.*
import kotlinx.android.synthetic.main.time_picker_dialog.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 */
class TimeConfFragment : DialogFragment(), TimeConfListener {
    private lateinit var viewModel: ShareViewModel
    private lateinit var timeConfAdapter: TimeConfAdapter
    private var typeTimeConf by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FulllScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_conf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ShareViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        timeConfAdapter = TimeConfAdapter(requireContext(), this)
        rvDifferentTime.apply {
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            adapter = timeConfAdapter
        }

        toolbarTimeConf.navigationIcon =
                ContextCompat.getDrawable(view.context, R.drawable.ic_arrow_back)
        toolbarTimeConf.setTitleTextColor(Color.WHITE)
        toolbarTimeConf.setNavigationOnClickListener {
            dismiss()
        }

        typeTimeConf = arguments?.getSerializable(("typeTimeConf")) as Int

        when(typeTimeConf){
            HomeFragment.restRoundsType -> {
                toolbarTimeConf.title = getString(R.string.time_conf_rest_round_message)
                switchSameTime.isChecked = viewModel.sameTimeRestRounds.value!!
            }
            HomeFragment.seriesTimeType -> {
                toolbarTimeConf.title = getString(R.string.time_conf_time_series_message)
                switchSameTime.isChecked = viewModel.sameTimeSeries.value!!
            }
            HomeFragment.restSeriesType -> {
                toolbarTimeConf.title = getString(R.string.time_conf_rest_series_message)
                switchSameTime.isChecked = viewModel.sameTimeRestSeries.value!!
            }
        }
        if (!switchSameTime.isChecked){ //different time
            llDifferentTime.visibility = View.VISIBLE
            llSameTime.visibility = View.GONE
            createLayout(typeTimeConf)
        }else{
            var timeInMili = 0L
            when(typeTimeConf){
                HomeFragment.restRoundsType -> {
                    timeInMili = viewModel.intervalTime.value!!.restTimeRounds.get(0)
                }
                HomeFragment.seriesTimeType -> {
                    timeInMili = viewModel.intervalTime.value!!.seriesTime.get(0)
                }
                HomeFragment.restSeriesType -> {
                    timeInMili = viewModel.intervalTime.value!!.seriesTime.get(1)
                }
            }
            val seconds = timeInMili?.div(1000)?.rem(60)
            val minutes = timeInMili?.div(1000)?.div(60)
            edSameTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds), TextView.BufferType.EDITABLE)
        }

        switchSameTime.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                llDifferentTime.visibility = View.GONE
                llSameTime.visibility = View.VISIBLE
                var timeInMili = 0L
                when(typeTimeConf){
                    HomeFragment.restRoundsType -> {
                        viewModel.sameTimeRestRounds.value = true
                        timeInMili = viewModel.intervalTime.value!!.restTimeRounds.get(0)
                    }
                    HomeFragment.seriesTimeType -> {
                        viewModel.sameTimeSeries.value = true
                        timeInMili = viewModel.intervalTime.value!!.seriesTime.get(0)
                    }
                    HomeFragment.restSeriesType -> {
                        viewModel.sameTimeRestSeries.value = true
                        timeInMili = viewModel.intervalTime.value!!.seriesTime.get(1)
                    }
                }
                val seconds = timeInMili?.div(1000)?.rem(60)
                val minutes = timeInMili?.div(1000)?.div(60)
                edSameTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds), TextView.BufferType.EDITABLE)
            } else {
                llDifferentTime.visibility = View.VISIBLE
                llSameTime.visibility = View.GONE
                when(typeTimeConf){
                    HomeFragment.restRoundsType -> {
                        viewModel.sameTimeRestRounds.value = false
                    }
                    HomeFragment.seriesTimeType -> {
                        viewModel.sameTimeSeries.value = false
                    }
                    HomeFragment.restSeriesType -> {
                        viewModel.sameTimeRestSeries.value = false
                    }
                }
                createLayout(typeTimeConf)
            }
        }

        showTimerPickerDialog(edSameTime)

        ivSameTimeAdd.setOnClickListener {
            Utils.addTime(edSameTime)
            updateData()
        }

        ivSameTimeRemove.setOnClickListener {
            Utils.removeTime(edSameTime)
            updateData()
        }

        bStart.setOnClickListener {
            dismiss()
        }
    }

    private fun createLayout(typeTimeConf:Int){
        when(typeTimeConf){
            HomeFragment.restRoundsType -> {
                var roundsList: MutableList<HashMap<String,Long>> = ArrayList<HashMap<String,Long>>()
                for (i in 0 until viewModel.intervalTime.value!!.roundsNumber){
                    val map = hashMapOf<String, Long>(resources.getString(R.string.round_time_conf_title, i+1) to viewModel.intervalTime.value!!.restTimeRounds[i])
                    roundsList.add(map)
                }
                timeConfAdapter.updateData(roundsList)
            }
            HomeFragment.seriesTimeType -> {
                var seriesList: MutableList<HashMap<String,Long>> = ArrayList<HashMap<String,Long>>()
                for (i in 0 until viewModel.intervalTime.value!!.seriesNumber step 2){
                    val map = hashMapOf<String, Long>(resources.getString(R.string.serie_time_conf_title, (i / 2) + 1) to viewModel.intervalTime.value!!.seriesTime[i])
                    seriesList.add(map)
                }
                timeConfAdapter.updateData(seriesList)
            }
            HomeFragment.restSeriesType -> {
                var seriesList: MutableList<HashMap<String,Long>> = ArrayList<HashMap<String,Long>>()
                for (i in 1 until viewModel.intervalTime.value!!.seriesNumber step 2){
                    val map = hashMapOf<String, Long>(resources.getString(R.string.serie_time_conf_title, (i / 2) + 1) to viewModel.intervalTime.value!!.seriesTime[i])
                    seriesList.add(map)
                }
                timeConfAdapter.updateData(seriesList)
            }
        }
    }

    override fun onUpdateDataListener(position: Int, editText: EditText) {
        val mili: Long = Utils.getMiliFromEt(editText)

        when(typeTimeConf){
            HomeFragment.restRoundsType -> {
                viewModel.intervalTime.value?.restTimeRounds?.set(position, mili)
            }
            HomeFragment.seriesTimeType -> {
                viewModel.intervalTime.value?.seriesTime?.set(position*2, mili)
            }
            HomeFragment.restSeriesType -> {
                viewModel.intervalTime.value?.seriesTime?.set(position*2+1, mili)
            }
        }
    }

    private fun showTimerPickerDialog(editText: EditText){
        editText.setOnClickListener {
            val mDialogView = LayoutInflater.from(ContextThemeWrapper(context, R.style.NumberPickerTextColorStyle)).inflate(R.layout.time_picker_dialog, null)
            val mBuilder = AlertDialog.Builder(this.context)
                    .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.setCanceledOnTouchOutside(true)
            mAlertDialog.bAcceptPicker.setOnClickListener {
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", mAlertDialog.minutes.value, mAlertDialog.seconds.value), TextView.BufferType.EDITABLE)
                updateData()
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

    private fun updateData() {
        var rounds = viewModel.intervalTime.value?.roundsNumber!!
        var series = viewModel.intervalTime.value?.seriesNumber!!

        when(typeTimeConf){
            HomeFragment.restRoundsType -> {
                val miliRounds = Utils.getMiliFromEt(edSameTime)
                for (i in 0 until rounds) { //series time
                    viewModel.intervalTime.value?.restTimeRounds!![i] = miliRounds
                }
            }
            HomeFragment.seriesTimeType -> {
                val miliSeries = Utils.getMiliFromEt(edSameTime)
                for (i in 0 until series step 2) { //series time
                    viewModel.intervalTime.value?.seriesTime!![i] = miliSeries
                }
            }
            HomeFragment.restSeriesType -> {
                val miliRest = Utils.getMiliFromEt(edSameTime)
                for (i in 1 until series step 2) { //rest time
                    viewModel.intervalTime.value?.seriesTime!![i] = miliRest
                }
            }
        }

        viewModel.intervalTime.value?.reset()
    }
}
