package com.zelyder.todoapp

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.zelyder.todoapp.di.components.AppComponent
import com.zelyder.todoapp.di.components.DaggerAppComponent
import com.zelyder.todoapp.presentation.background.ReminderWorker
import com.zelyder.todoapp.presentation.background.UpdateWorker
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class MyApp : Application() {
    lateinit var appComponent: AppComponent

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()

        initDagger()

        appComponent.inject(this)

        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )

        ReminderWorker.startWork(applicationContext)
        UpdateWorker.startWork(applicationContext)
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.factory().create(this.applicationContext)
    }

}

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
val Context.appComponent: AppComponent
    get() = when (this) {
        is MyApp -> appComponent
        else -> this.applicationContext.appComponent
    }