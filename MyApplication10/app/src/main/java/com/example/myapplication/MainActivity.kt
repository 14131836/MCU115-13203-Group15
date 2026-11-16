package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnStartFocus: Button
    private lateinit var btnStop: Button

    private var isReceiverRegistered = false

    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.ACTION_TIMER_UPDATE) {
                val formattedTime = intent.getStringExtra(TimerService.EXTRA_FORMATTED_TIME) ?: "00:00"
                val remainingSeconds = intent.getIntExtra(TimerService.EXTRA_REMAINING_SECONDS, 0)

                tvTimer.text = formattedTime

                if (remainingSeconds > 0) {
                    setFocusingState()
                } else {
                    setReadyState()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvStatus = findViewById(R.id.tvStatus)
        tvTimer = findViewById(R.id.tvTimer)
        btnStartFocus = findViewById(R.id.btnStartFocus)
        btnStop = findViewById(R.id.btnStop)

        btnStartFocus.setOnClickListener {
            startFocusSession()
        }

        btnStop.setOnClickListener {
            stopFocusSession()
        }
    }

    override fun onStart() {
        super.onStart()
        registerTimerReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterTimerReceiver()
    }

    private fun startFocusSession() {
        registerTimerReceiver()
        val serviceIntent = Intent(this, TimerService::class.java)
        startService(serviceIntent)
        setFocusingState()
    }

    private fun stopFocusSession() {
        val serviceIntent = Intent(this, TimerService::class.java)
        stopService(serviceIntent)
        setReadyState()
        tvTimer.text = getString(R.string.initial_timer)
    }

    private fun setFocusingState() {
        tvStatus.text = getString(R.string.status_focusing)
        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.primary_red))
    }

    private fun setReadyState() {
        tvStatus.text = getString(R.string.status_ready)
        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_green))
    }

    private fun registerTimerReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter(TimerService.ACTION_TIMER_UPDATE)
            ContextCompat.registerReceiver(this, timerUpdateReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
            isReceiverRegistered = true
        }
    }

    private fun unregisterTimerReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(timerUpdateReceiver)
            } catch (_: Exception) {
            }
            isReceiverRegistered = false
        }
    }
}