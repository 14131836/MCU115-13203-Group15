package com.example.myapplication

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

enum class TaskStatus {
    IDLE,
    PREPARING,
    WORKING,
    FINISHED,
    CANCELED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _taskStatus = MutableLiveData<TaskStatus>(TaskStatus.IDLE)
    val taskStatus: LiveData<TaskStatus> = _taskStatus

    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int> = _progress

    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var workerThread: Thread? = null

    private var mediaPlayer: MediaPlayer? = null

    fun startWork() {
        stopWorkerThread()
        stopMusic()

        _taskStatus.value = TaskStatus.PREPARING
        _progress.value = 0

        // Start background music when background work starts
        playMusic()

        val newThread = Thread {
            val currentThread = Thread.currentThread()
            try {
                // Preparation phase (1.5s delay)
                Thread.sleep(1500)

                if (currentThread != workerThread || currentThread.isInterrupted) {
                    mainHandler.post { stopMusic() }
                    return@Thread
                }

                // Transition to working state
                postStatusIfActive(currentThread, TaskStatus.WORKING)
                postProgressIfActive(currentThread, 0)

                // Working phase: progress from 1 to 100
                for (i in 1..100) {
                    if (currentThread != workerThread || currentThread.isInterrupted) {
                        mainHandler.post { stopMusic() }
                        return@Thread
                    }

                    postProgressIfActive(currentThread, i)
                    Thread.sleep(50)
                }

                if (currentThread == workerThread && !currentThread.isInterrupted) {
                    postStatusIfActive(currentThread, TaskStatus.FINISHED)
                    postProgressIfActive(currentThread, 100)
                    mainHandler.post { stopMusic() }
                }

            } catch (e: InterruptedException) {
                if (currentThread == workerThread) {
                    postStatus(TaskStatus.CANCELED)
                    mainHandler.post { stopMusic() }
                }
            }
        }

        workerThread = newThread
        newThread.start()
    }

    fun cancelWork() {
        stopWorkerThread()
        stopMusic()
        _taskStatus.value = TaskStatus.CANCELED
    }

    @Synchronized
    private fun stopWorkerThread() {
        workerThread?.let {
            if (it.isAlive) {
                it.interrupt()
            }
        }
        workerThread = null
    }

    private fun playMusic() {
        try {
            stopMusic()
            val mp = MediaPlayer.create(getApplication(), R.raw.background_music)
            if (mp != null) {
                mediaPlayer = mp.apply {
                    isLooping = true
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
    }

    private fun postStatusIfActive(thread: Thread, status: TaskStatus) {
        mainHandler.post {
            if (thread == workerThread) {
                _taskStatus.value = status
            }
        }
    }

    private fun postProgressIfActive(thread: Thread, prog: Int) {
        mainHandler.post {
            if (thread == workerThread) {
                _progress.value = prog
            }
        }
    }

    private fun postStatus(status: TaskStatus) {
        mainHandler.post {
            _taskStatus.value = status
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopWorkerThread()
        stopMusic()
    }
}
