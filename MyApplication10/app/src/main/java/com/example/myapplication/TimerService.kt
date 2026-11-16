package com.example.myapplication

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlin.math.ceil

class TimerService : Service() {

    companion object {
        const val ACTION_TIMER_UPDATE = "com.example.myapplication.TIMER_UPDATE"
        const val EXTRA_REMAINING_SECONDS = "extra_remaining_seconds"
        const val EXTRA_FORMATTED_TIME = "extra_formatted_time"
        const val DEFAULT_DURATION_SECONDS = 25 * 60 // 25 minutes = 1500s
    }

    private var remainingSeconds = DEFAULT_DURATION_SECONDS
    @Volatile
    private var isRunning = false
    private var timerThread: Thread? = null

    private val screenOnReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if ((intent?.action == Intent.ACTION_SCREEN_ON) && isRunning) {
                val remainingMins = ceil(remainingSeconds.toDouble() / 60.0).toInt()
                val message = "Focus Reminder: $remainingMins mins remaining!"
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        ContextCompat.registerReceiver(this, screenOnReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            remainingSeconds = intent?.getIntExtra(EXTRA_REMAINING_SECONDS, DEFAULT_DURATION_SECONDS)
                ?: DEFAULT_DURATION_SECONDS
            startTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        isRunning = true
        timerThread = Thread {
            try {
                while (isRunning && remainingSeconds > 0) {
                    val formattedTime = formatTime(remainingSeconds)
                    broadcastTimerUpdate(remainingSeconds, formattedTime)

                    Thread.sleep(1000)
                    remainingSeconds--
                }

                if (isRunning && remainingSeconds <= 0) {
                    broadcastTimerUpdate(0, "00:00")
                    onTimerFinished()
                }
            } catch (_: InterruptedException) {
                // Thread interrupted when service stops
            }
        }
        timerThread?.start()
    }

    private fun broadcastTimerUpdate(seconds: Int, formattedTime: String) {
        val intent = Intent(ACTION_TIMER_UPDATE).apply {
            putExtra(EXTRA_REMAINING_SECONDS, seconds)
            putExtra(EXTRA_FORMATTED_TIME, formattedTime)
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }

    private fun onTimerFinished() {
        isRunning = false
        val finishIntent = Intent(this, FinishActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(finishIntent)
        stopSelf()
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        timerThread?.interrupt()
        timerThread = null
        try {
            unregisterReceiver(screenOnReceiver)
        } catch (_: Exception) {
            // Receiver might not be registered
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}