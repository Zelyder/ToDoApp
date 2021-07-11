package com.zelyder.todoapp.presentation.background

import android.content.Context
import android.util.Log
import androidx.work.*
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.presentation.core.Notifications
import com.zelyder.todoapp.presentation.core.calculateTimeDiffInMillis
import java.util.concurrent.TimeUnit

class UpdateWorker (val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            updateData()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    companion object {
        private const val UPDATE_WORKER_TAG = "UpdateWorker"
        private const val PERIOD_HOUR = 8L
        private lateinit var tasksListRepository: TasksListRepository

        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        private val request =
            PeriodicWorkRequest.Builder(
                ReminderWorker::class.java, PERIOD_HOUR, TimeUnit.HOURS
            )
                .addTag(UPDATE_WORKER_TAG)
                .setConstraints(constraints)
                .build()

        fun startWork(context: Context, _tasksListRepository: TasksListRepository) {
            tasksListRepository = _tasksListRepository
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(UPDATE_WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, request)
        }

        private suspend fun updateData() {
            tasksListRepository.checkInternetAndSync()
        }
    }
}