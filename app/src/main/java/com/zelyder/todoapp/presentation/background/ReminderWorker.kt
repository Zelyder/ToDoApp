package com.zelyder.todoapp.presentation.background

import android.content.Context
import android.util.Log
import androidx.work.*
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.presentation.core.Notifications
import com.zelyder.todoapp.presentation.core.calculateTimeDiffInMillis
import java.util.concurrent.TimeUnit

class ReminderWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            updateData()
            Log.d("LOL", "doWork")
            sendNotification(context)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val REMINDER_TAG = "ReminderWorker"
        private const val START_HOUR = 9
        private const val START_MINUTE = 0
        private lateinit var tasksListRepository: TasksListRepository
        private var countTasksToDo: Int? = null

        fun startWork(context: Context, _tasksListRepository: TasksListRepository) {

            val time = calculateTimeDiffInMillis(START_HOUR, START_MINUTE)
            val request =
                OneTimeWorkRequest.Builder(
                    ReminderWorker::class.java
                )
                    .setInitialDelay(time, TimeUnit.MILLISECONDS)
                    .addTag(REMINDER_TAG)
                    .build()

            tasksListRepository = _tasksListRepository
            WorkManager.getInstance(context).enqueueUniqueWork(REMINDER_TAG, ExistingWorkPolicy.REPLACE, request)
        }

        private suspend fun updateData() {
            countTasksToDo = tasksListRepository.getCountTodayTasks()
        }

        private fun sendNotification(context: Context) {
            countTasksToDo?.let { if (it > 0) Notifications().show(context, it) }
            startWork(context, tasksListRepository)
        }
    }
}