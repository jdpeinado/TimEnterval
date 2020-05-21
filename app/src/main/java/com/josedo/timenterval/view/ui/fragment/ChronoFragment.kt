package com.josedo.timenterval.view.ui.fragment

import android.app.AlertDialog
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.josedo.timenterval.R
import com.josedo.timenterval.viewmodel.ShareViewModel
import kotlinx.android.synthetic.main.dialog_error.*
import kotlinx.android.synthetic.main.fragment_chrono.*
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ChronoFragment : DialogFragment() {
    private lateinit var viewModel: ShareViewModel

    private var mCountDownTimer: CountDownTimer? = null

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

        toolbarChronometer.navigationIcon =
                ContextCompat.getDrawable(view.context, R.drawable.ic_arrow_back)
        toolbarChronometer.setTitleTextColor(Color.WHITE)
        toolbarChronometer.setNavigationOnClickListener {
            if (viewModel.mTimerRunning.value!!) {
                showDialogMessage(R.string.chrono_without_finnised)
            } else {
                mCountDownTimer?.cancel()
                dismiss()
            }
        }
        toolbarChronometer.title = getString(R.string.app_name)

        bPause.setOnClickListener {
            if (viewModel.mTimerRunning.value!!) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        bReset.setOnClickListener {
            if (viewModel.mTimerRunning.value!!) {
                pauseTimer()
            }
            viewModel.intervalTime.value?.reset()
            updateCountDownText()
            startTimer()
        }

        updateCountDownText()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                if (viewModel.mTimerRunning.value!!) {

                    showDialogMessage(R.string.chrono_without_finnised)
                    true
                } else {
                    mCountDownTimer?.cancel()
                    dismiss()
                    true
                }
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
                            viewModel.mTimerRunning.value = false
                            bPause.visibility = View.INVISIBLE
                            viewModel.intervalTime.value?.reset()
                            val timeLeftFormatted: String =
                                    java.lang.String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d,%02d",
                                            0,
                                            0,
                                            0
                                    )
                            chronometer.text = timeLeftFormatted
                        } else {
                            mCountDownTimer?.cancel()
                            viewModel.intervalTime.value?.setNextTime()
                            startTimer()
                        }
                    }
                }.start()
        Log.d("ChronoFragment", "Starting a new countDownTimer")
        viewModel.mTimerRunning.value = true
        bPause.setText(resources.getString(R.string.pause))
    }

    private fun pauseTimer() {
        mCountDownTimer?.cancel()
        viewModel.mTimerRunning.value = false
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

        if (chronometer != null) {
            chronometer.text = timeLeftFormatted

            if (!viewModel.soundIsPlaying.value!!) {
                if (viewModel.intervalTime.value!!.isPreparing || viewModel.intervalTime.value!!.isRestRound || viewModel.intervalTime.value!!.actualSerie.rem(2) != 0) {
                    if (minutes == 0 && (seconds == 3 || seconds == 2 || seconds == 1 || seconds == 0) && milisecondsInTenths > 90L) {
                        Log.d("chronoFragment", "second is 3")
                        viewModel.soundIsPlaying.value = true

                        playCountDownSound(seconds == 0)
                    }
                } else {
                    if (minutes == 0 && seconds == 0 && milisecondsInTenths > 70L) {
                        Log.d("chronoFragment", "second is 3")
                        viewModel.soundIsPlaying.value = true

                        playCountDownSound(seconds == 0)
                    }
                }
            }

            if (viewModel.intervalTime.value?.isPreparing!!) {
                tvTitleRoundsSeries.text = resources.getString(R.string.preparing_message)
                chronometer.setTextColor(resources.getColor(R.color.colorSeries))
            } else if (viewModel.intervalTime.value?.isRestRound!!) {
                tvTitleRoundsSeries.text = resources.getString(
                        R.string.rest_round_message,
                        viewModel.intervalTime.value?.actualRound?.plus(1)
                )
                chronometer.setTextColor(resources.getColor(R.color.colorRounds))
            } else {
                if (viewModel.intervalTime.value?.actualSerie?.plus(1)?.rem(2) == 0) {
                    tvTitleRoundsSeries.text = resources.getString(
                            R.string.rest_serie_message,
                            viewModel.intervalTime.value?.actualRound?.plus(1),
                            (viewModel.intervalTime.value?.actualSerie!! / 2) + 1
                    )
                    chronometer.setTextColor(resources.getColor(R.color.colorRounds))
                } else {
                    tvTitleRoundsSeries.text = resources.getString(R.string.round_serie_message, viewModel.intervalTime.value?.actualRound?.plus(1), (viewModel.intervalTime.value?.actualSerie!!.plus(1) / 2) + 1)
                    chronometer.setTextColor(resources.getColor(R.color.colorSeries))
                }
            }
        }
    }

    private fun playCountDownSound(last: Boolean) {
        Log.d("chronoFragment", "sound is on")
        var duration = 100
        if (last)
            duration = 1000
        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        toneG.startTone(ToneGenerator.TONE_DTMF_S, duration)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            toneG.release()
            Log.d("chronoFragment", "sound is off")
            viewModel.soundIsPlaying.value = false
        }, (duration + 10).toLong())
    }

    private fun showDialogMessage(message: Int) {
        val mDialogView = LayoutInflater.from(this.context).inflate(R.layout.dialog_error, null)
        val mBuilder = AlertDialog.Builder(this.context)
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.tvTitleDialog.text = resources.getString(R.string.title_error_message)
        mAlertDialog.tvMessageDialog.text = resources.getString(message)
        mAlertDialog.bCloseDialog.setOnClickListener {
            mCountDownTimer?.cancel()
            mAlertDialog.dismiss()
            dismiss()
        }
        mAlertDialog.bAccept.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mAlertDialog.setCanceledOnTouchOutside(true)
    }
}
