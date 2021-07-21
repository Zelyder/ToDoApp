package com.zelyder.todoapp.presentation.background

import android.content.Context
import androidx.work.*
import com.zelyder.todoapp.appComponent
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.presentation.core.Notifications
import com.zelyder.todoapp.presentation.core.calculateTimeDiffInMillis
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class ReminderWorker (
    val context: Context,
    params: WorkerParameters,
    private val tasksListRepository: TasksListRepository,
) :
    CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        return try {
            countTasksToDo = tasksListRepository.getCountTodayTasks()
            sendNotification(context)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }


    class Factory @Inject constructor(
        private val tasksListRepository: TasksListRepository
    ) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return ReminderWorker(appContext, params, tasksListRepository)
        }

    }

    companion object {
        private const val REMINDER_TAG = "ReminderWorker"
        private const val START_HOUR = 9
        private const val START_MINUTE = 0

        private var countTasksToDo: Int? = null


        fun startWork(context: Context) {

            val time = calculateTimeDiffInMillis(START_HOUR, START_MINUTE)
            val request =
                OneTimeWorkRequest.Builder(
                    ReminderWorker::class.java
                )
                    .setInitialDelay(time, TimeUnit.MILLISECONDS)
                    .addTag(REMINDER_TAG)
                    .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(REMINDER_TAG, ExistingWorkPolicy.REPLACE, request)
        }

        private fun sendNotification(context: Context) {
            countTasksToDo?.let { if (it > 0) Notifications().show(context, it) }
            startWork(context)
        }
    }

}