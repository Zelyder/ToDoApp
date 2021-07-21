package com.zelyder.todoapp

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.zelyder.todoapp.data.network.TasksNetworkModule
import com.zelyder.todoapp.data.storage.db.TasksDb
import com.zelyder.todoapp.di.AppComponent
import com.zelyder.todoapp.di.DaggerAppComponent
import com.zelyder.todoapp.domain.datasources.DeletedTasksDataSourceImpl
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSourceImpl
import com.zelyder.todoapp.domain.datasources.TasksYandexDataSourceImpl
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.domain.repositories.TasksListRepositoryImpl
import com.zelyder.todoapp.presentation.background.MyWorkerFactory
import com.zelyder.todoapp.presentation.background.ReminderWorker
import com.zelyder.todoapp.presentation.background.UpdateWorker
import com.zelyder.todoapp.presentation.core.NetworkStatusTracker
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import com.zelyder.todoapp.presentation.core.ViewModelFactoryProvider
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