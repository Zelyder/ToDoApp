package com.zelyder.todoapp.presentation.background

import android.content.Context
import android.util.Log
import androidx.work.*
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.presentation.core.Notifications
import java.util.concurrent.TimeUnit

class ReminderWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            updateData()
            sendNotification(context)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "ReminderWorker"
        private const val TIME_PERIOD_IN_HOURS = 24L
        private lateinit var tasksListRepository: TasksListRepository
        private var countTasksToDo: Int? = null

        private val constraints = Constraints.Builder()
            .build()

        private val request =
            PeriodicWorkRequest.Builder(
                ReminderWorker::class.java,
                TIME_PERIOD_IN_HOURS,
                TimeUnit.HOURS
            )
                .addTag(TAG)
                .setConstraints(constraints)
                .build()

        fun startWork(context: Context, _tasksListRepository: TasksListRepository) {
            tasksListRepository = _tasksListRepository
            WorkManager.getInstance(context).enqueue(request)
        }

        private fun updateData() {
            countTasksToDo = tasksListRepository.getCountTodayTasks()
        }

        private fun sendNotification(context: Context) {
            countTasksToDo?.let { if (it > 0) Notifications().show(context, it) }
        }
    }
}