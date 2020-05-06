package com.josedo.timenterval.view.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.josedo.timenterval.R
import com.josedo.timenterval.utils.OnBackPressed
import com.josedo.timenterval.viewmodel.ShareViewModel
import kotlinx.android.synthetic.main.fragment_chrono.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ChronoFragment : DialogFragment() {
    private lateinit var viewModel: ShareViewModel

    private var mCountDownTimer: CountDownTimer? = null

    private var mTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FulllScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chrono, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ShareViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        viewModel.refresh()

        toolbarChronometer.navigationIcon =
            ContextCompat.getDrawable(view.context, R.drawable.ic_arrow_back)
        toolbarChronometer.setTitleTextColor(Color.WHITE)
        toolbarChronometer.setNavigationOnClickListener {
            mCountDownTimer?.cancel()
            dismiss()
        }
        toolbarChronometer.title = getString(R.string.app_name)

        bPause.setOnClickListener {
            if (mTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        updateCountDownText()
        startTimer()
    }

    //Pressed return button - returns to the results menu
    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                mCountDownTimer?.cancel()
                dismiss()
                true
            } else false
        }
    }

    private fun startTimer() {
        mCountDownTimer =
            object : CountDownTimer(viewModel.intervalTime.value?.mTimeLeftInMillis!!, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    viewModel.intervalTime.value?.mTimeLeftInMillis = millisUntilFinished
                    updateCountDownText()
                }

                override fun onFinish() {
                    if (viewModel.intervalTime.value?.isEnd()!!) {
                        mTimerRunning = false
                        bPause.visibility = View.INVISIBLE
                    } else {
                        mCountDownTimer?.cancel()
                        viewModel.intervalTime.value?.setNextTime()
                        startTimer()
                    }
                }
            }.start()
        Log.d("ChronoFragment", "Starting a new countDownTimer")
        mTimerRunning = true
        bPause.setText(resources.getString(R.string.pause))
    }

    private fun pauseTimer() {
        mCountDownTimer?.cancel()
        mTimerRunning = false
        bPause.setText(resources.getString(R.string.start))
    }

    private fun updateCountDownText() {
        val minutes = (viewModel.intervalTime.value?.mTimeLeftInMillis!! / 1000).toInt() / 60
        val seconds = (viewModel.intervalTime.value?.mTimeLeftInMillis!! / 1000).toInt() % 60
        var milisecondsInTenths: Long =
            (viewModel.intervalTime.value?.mTimeLeftInMillis!! - ((viewModel.intervalTime.value?.mTimeLeftInMillis!! / 1000) * 1000)) / 10
        val timeLeftFormatted: String =
            java.lang.String.format(
                Locale.getDefault(),
                "%02d:%02d,%02d",
                minutes,
                seconds,
                milisecondsInTenths
            )
        chronometer.text = timeLeftFormatted

        if (viewModel.intervalTime.value?.isRestRound!!) {
            tvTitleRoundsSeries.text = resources.getString(
                R.string.rest_round_message,
                viewModel.intervalTime.value?.actualRound?.plus(1)
            )
            chronometer.setTextColor(resources.getColor(R.color.colorRounds))
        }else {
            if (viewModel.intervalTime.value?.actualSerie?.plus(1)?.rem(2) == 0) {
                tvTitleRoundsSeries.text = resources.getString(
                    R.string.rest_serie_message,
                    viewModel.intervalTime.value?.actualRound?.plus(1),
                    (viewModel.intervalTime.value?.actualSerie!! / 2) + 1
                )
                chronometer.setTextColor(resources.getColor(R.color.colorRounds))
            }else {
                tvTitleRoundsSeries.text = resources.getString(R.string.round_serie_message, viewModel.intervalTime.value?.actualRound?.plus(1), (viewModel.intervalTime.value?.actualSerie!!.plus(1) / 2) + 1)
                chronometer.setTextColor(resources.getColor(R.color.colorSeries))
            }
        }
    }
}
