package com.zelyder.todoapp

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.zelyder.todoapp.data.network.TasksNetworkModule
import com.zelyder.todoapp.data.storage.db.TasksDb
import com.zelyder.todoapp.domain.datasources.DeletedTasksDataSourceImpl
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSourceImpl
import com.zelyder.todoapp.domain.datasources.TasksYandexDataSourceImpl
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.domain.repositories.TasksListRepositoryImpl
import com.zelyder.todoapp.presentation.background.ReminderWorker
import com.zelyder.todoapp.presentation.background.UpdateWorker
import com.zelyder.todoapp.presentation.core.NetworkStatusTracker
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import com.zelyder.todoapp.presentation.core.ViewModelFactoryProvider
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class MyApp : Application(), ViewModelFactoryProvider {
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var tasksListRepository: TasksListRepository
    val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        initRepositories()


        ReminderWorker.startWork(applicationContext, tasksListRepository)
        UpdateWorker.startWork(applicationContext, tasksListRepository)
        viewModelFactory = ViewModelFactory(tasksListRepository)
    }


    override fun viewModelFactory(): ViewModelFactory = viewModelFactory

    private fun initRepositories() {
        val tasksDb = TasksDb.create(applicationContext)
        val tasksLocalDataSource = TasksLocalDataSourceImpl(tasksDb)
        val yandexDataSource = TasksYandexDataSourceImpl(TasksNetworkModule().yandexApi())
        val deletedTasksDataSource = DeletedTasksDataSourceImpl(tasksDb.deletedTasksDao())

        tasksListRepository = TasksListRepositoryImpl(
            tasksLocalDataSource,
            deletedTasksDataSource,
            yandexDataSource,
            NetworkStatusTracker(applicationContext)
        )

        scope.launch {
            tasksListRepository.checkInternetAndSync()
        }
    }

}

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
fun Context.viewModelFactoryProvider() = (applicationContext as MyApp)

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
fun Fragment.viewModelFactoryProvider() = requireContext().viewModelFactoryProvider()