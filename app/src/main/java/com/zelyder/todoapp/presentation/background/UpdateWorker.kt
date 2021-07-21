package com.zelyder.todoapp.presentation.background

import android.content.Context
import android.util.Log
import androidx.work.*
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.presentation.core.Notifications
import com.zelyder.todoapp.presentation.core.calculateTimeDiffInMillis
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UpdateWorker (
    val context: Context,
    params: WorkerParameters,
    private val tasksListRepository: TasksListRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            tasksListRepository.checkInternetAndSync()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }


    class Factory @Inject constructor(
        private val tasksListRepository: TasksListRepository
    ) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return UpdateWorker(appContext, params, tasksListRepository)
        }

    }

    @ExperimentalSerializationApi
    @ExperimentalCoroutinesApi
    companion object {
        private const val UPDATE_WORKER_TAG = "UpdateWorker"
        private const val PERIOD_HOUR = 8L

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

        fun startWork(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UPDATE_WORKER_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }
}