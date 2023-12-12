package com.example.taskflow

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.concurrent.TimeUnit

class Home : Fragment(R.layout.fragment_home) {

    private lateinit var timerTextView: TextView
    private lateinit var startPauseButton: Button
    private lateinit var resetButton: Button

    private lateinit var timer: CountDownTimer
    private var timerRunning = false
    private var timeRemainingMillis: Long = 0
    private var startTimeMillis: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        timerTextView = view.findViewById(R.id.timeTV)
        startPauseButton = view.findViewById(R.id.start_pause_button)
        resetButton = view.findViewById(R.id.resetButton)

        startPauseButton.setOnClickListener { toggleTimer() }
        resetButton.setOnClickListener { resetTimer() }

        // Restore timer state if fragment is recreated
        if (savedInstanceState != null) {
            timerRunning = savedInstanceState.getBoolean(KEY_TIMER_RUNNING)
            timeRemainingMillis = savedInstanceState.getLong(KEY_TIME_REMAINING)
            startTimeMillis = savedInstanceState.getLong(KEY_START_TIME)
            updateTimerText()
            updateButtons()
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_TIMER_RUNNING, timerRunning)
        outState.putLong(KEY_TIME_REMAINING, timeRemainingMillis)
        outState.putLong(KEY_START_TIME, startTimeMillis)
    }

    private fun toggleTimer() {
        if (timerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
        updateButtons()
    }

    private fun startTimer() {
        if (!timerRunning) {
            timer = object : CountDownTimer(timeRemainingMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingMillis = millisUntilFinished
                    updateTimerText()
                }

                override fun onFinish() {
                    timerRunning = false
                    updateButtons()
                }
            }.start()

            if (startTimeMillis == 0L) {
                startTimeMillis = System.currentTimeMillis() - (TIMER_DURATION - timeRemainingMillis)
            }

            timerRunning = true
        }
    }

    private fun pauseTimer() {
        timer.cancel()
        timerRunning = false
    }

    private fun resetTimer() {
        timer.cancel()
        timerRunning = false
        timeRemainingMillis = TIMER_DURATION.toLong()
        startTimeMillis = 0
        updateTimerText()
        updateButtons()
    }

    private fun updateTimerText() {
        val hours = TimeUnit.MILLISECONDS.toHours(timeRemainingMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemainingMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemainingMillis) % 60
        timerTextView.text = makeTimeString(hours, minutes, seconds)
    }

    private fun updateButtons() {
        if (timerRunning) {
            startPauseButton.text = getString(R.string.start)
            resetButton.isEnabled = false
        } else {
            startPauseButton.text = getString(R.string.stop)
            resetButton.isEnabled = true
        }
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    companion object {
        private const val TIMER_DURATION = 1500000 // 1 minute in milliseconds

        private const val KEY_TIMER_RUNNING = "timer_running"
        private const val KEY_TIME_REMAINING = "time_remaining"
        private const val KEY_START_TIME = "start_time"
    }
}