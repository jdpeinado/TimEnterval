package com.josedo.timenterval.view.ui.fragment


import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.josedo.timenterval.R
import com.josedo.timenterval.utils.Utils
import com.josedo.timenterval.viewmodel.ShareViewModel
import kotlinx.android.synthetic.main.dialog_error.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.time_picker_dialog.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    companion object {
        val restRoundsType = 0
        val seriesTimeType = 1
        val restSeriesType = 2
        val maxMinutes = 99
        val maxSeconds = 59
        val maxRoundsOrSeries = 99
    }

    private lateinit var viewModel: ShareViewModel

    val etRoundsNumberWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateData()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
    val etSeriesNumberWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateData()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ShareViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        
        switchSameTime.isChecked = viewModel.sameTime.value!!
        switchChange(viewModel.sameTime.value!!)
        switchSameTime.setOnCheckedChangeListener { _, isChecked ->
            switchChange(isChecked)
        }
        ivRoundsAdd.setOnClickListener {
            addRoundsOrSeries(etRoundsNumber)
        }
        ivRoundsRemove.setOnClickListener {
            removeRoundsOrSeries(etRoundsNumber)
        }
        ivSeriesAdd.setOnClickListener {
            addRoundsOrSeries(etSeriesNumber)
        }
        ivSeriesRemove.setOnClickListener {
            removeRoundsOrSeries(etSeriesNumber)
        }

        etRoundsNumber.addTextChangedListener(etRoundsNumberWatcher)
        etSeriesNumber.addTextChangedListener(etSeriesNumberWatcher)

        showTimerPickerDialog(edTime)
        showTimerPickerDialog(edRestTime)
        showTimerPickerDialog(edPreparedTime)

        ivTimeAdd.setOnClickListener {
            Utils.addTime(edTime)
            updateData()
        }

        ivTimeRemove.setOnClickListener {
            Utils.removeTime(edTime)
            updateData()
        }

        ivRestTimeAdd.setOnClickListener {
            Utils.addTime(edRestTime)
            updateData()
        }

        ivRestTimeRemove.setOnClickListener {
            Utils.removeTime(edRestTime)
            updateData()
        }

        ivPreparedTimeAdd.setOnClickListener {
            Utils.addTime(edPreparedTime)
            updateData()
        }

        ivPreparedTimeRemove.setOnClickListener {
            Utils.removeTime(edPreparedTime)
            updateData()
        }

        bRestRounds.setOnClickListener {
            if (viewModel.intervalTime.value?.roundsNumber!! > 0) {
                val bundle = bundleOf("typeTimeConf" to restRoundsType)
                findNavController().navigate(R.id.timeConfFragment, bundle)
            } else {
                showDialogMessage(R.string.rounds_error_message)
            }
        }

        bSeriesTime.setOnClickListener {
            if (viewModel.intervalTime.value?.seriesNumber!! > 0) {
                val bundle = bundleOf("typeTimeConf" to seriesTimeType)
                findNavController().navigate(R.id.timeConfFragment, bundle)
            } else {
                showDialogMessage(R.string.series_error_message)
            }
        }

        bRestSeries.setOnClickListener {
            if (viewModel.intervalTime.value?.seriesNumber!! > 0) {
                val bundle = bundleOf("typeTimeConf" to restSeriesType)
                findNavController().navigate(R.id.timeConfFragment, bundle)
            } else {
                showDialogMessage(R.string.series_error_message)
            }
        }

        bStart.setOnClickListener {
            if (viewModel.intervalTime.value?.isReady()!!) {
                findNavController().navigate(R.id.chronoFragment)
            } else {
                showDialogMessage(R.string.series_time_error_message)
            }
        }
    }

    private fun updateData() {
        var rounds = etRoundsNumber.text.toString().toInt()
        var series = etSeriesNumber.text.toString().toInt() * 2

        viewModel.intervalTime.value?.setRounds(rounds)
        viewModel.intervalTime.value?.setSeries(series)

        if (viewModel.sameTime.value!!) {
            viewModel.intervalTime.value?.updateSeries(true,Utils.getMiliFromEt(edTime),false)
            viewModel.intervalTime.value?.updateSeries(true,Utils.getMiliFromEt(edRestTime),true)
        } else {
            viewModel.intervalTime.value?.updateSeries(false,0L,false)
        }
        val miliPrepared = Utils.getMiliFromEt(edPreparedTime)
        viewModel.intervalTime.value?.setPreparedTime(miliPrepared)

    }

    private fun switchChange(isChecked: Boolean) {
        if (isChecked) {
            llDifferentTime.visibility = View.GONE
            llSameTime.visibility = View.VISIBLE
            viewModel.sameTime.value = true
            updateData()
        } else {
            llDifferentTime.visibility = View.VISIBLE
            llSameTime.visibility = View.GONE
            viewModel.sameTime.value = false
            updateData()
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

    private fun showDialogMessage(message: Int) {
        val mDialogView = LayoutInflater.from(this.context).inflate(R.layout.dialog_error, null)
        val mBuilder = AlertDialog.Builder(this.context)
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.tvTitleDialog.text = resources.getString(R.string.title_error_message)
        mAlertDialog.tvMessageDialog.text = resources.getString(message)
        mAlertDialog.bCloseDialog.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mAlertDialog.setCanceledOnTouchOutside(true)
    }

    private fun addRoundsOrSeries(editText: EditText) {
        var num = editText.text.toString().toInt()
        if (num < maxRoundsOrSeries) {
            editText.setText(
                    (num + 1).toString(),
                    TextView.BufferType.EDITABLE
            )
            updateData()
        }
    }

    private fun removeRoundsOrSeries(editText: EditText) {
        var num = editText.text.toString().toInt()
        if (num > 1) {
            editText.setText(
                    (num - 1).toString(),
                    TextView.BufferType.EDITABLE
            )
            updateData()
        }
    }
}
