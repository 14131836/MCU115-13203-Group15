package com.example.myapplication

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class TaskStatus {
    IDLE,
    PREPARING,
    WORKING,
    FINISHED,
    CANCELED
}

class MainViewModel : ViewModel() {

    private val _taskStatus = MutableLiveData<TaskStatus>(TaskStatus.IDLE)
    val taskStatus: LiveData<TaskStatus> = _taskStatus

    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int> = _progress

    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var workerThread: Thread? = null

    fun startWork() {
        stopWorkerThread()

        _taskStatus.value = TaskStatus.PREPARING
        _progress.value = 0

        val newThread = Thread {
            val currentThread = Thread.currentThread()
            try {
                // Preparation phase (1.5s delay)
                Thread.sleep(1500)

                if (currentThread != workerThread || currentThread.isInterrupted) {
                    return@Thread
                }

                // Transition to working state
                postStatusIfActive(currentThread, TaskStatus.WORKING)
                postProgressIfActive(currentThread, 0)

                // Working phase: progress from 1 to 100
                for (i in 1..100) {
                    if (currentThread != workerThread || currentThread.isInterrupted) {
                        return@Thread
                    }

                    postProgressIfActive(currentThread, i)
                    Thread.sleep(50)
                }

                if (currentThread == workerThread && !currentThread.isInterrupted) {
                    postStatusIfActive(currentThread, TaskStatus.FINISHED)
                    postProgressIfActive(currentThread, 100)
                }

            } catch (e: InterruptedException) {
                if (currentThread == workerThread) {
                    postStatus(TaskStatus.CANCELED)
                }
            }
        }

        workerThread = newThread
        newThread.start()
    }

    fun cancelWork() {
        stopWorkerThread()
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
    }
}
