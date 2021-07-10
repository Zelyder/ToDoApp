package com.zelyder.todoapp

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.zelyder.todoapp.data.storage.db.TasksDb
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSourceImpl
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.domain.repositories.TasksListRepositoryImpl
import com.zelyder.todoapp.presentation.background.ReminderWorker
import com.zelyder.todoapp.presentation.core.Notifications
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import com.zelyder.todoapp.presentation.core.ViewModelFactoryProvider

class MyApp: Application(), ViewModelFactoryProvider {
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var tasksListRepository: TasksListRepository

    override fun onCreate() {
        super.onCreate()

        initRepositories()


        ReminderWorker.startWork(applicationContext, tasksListRepository)
        viewModelFactory = ViewModelFactory(tasksListRepository)
    }


    override fun viewModelFactory(): ViewModelFactory = viewModelFactory

    private fun initRepositories() {
        val tasksLocalDataSource = TasksLocalDataSourceImpl(TasksDb.create(applicationContext))

        tasksListRepository = TasksListRepositoryImpl(tasksLocalDataSource)
    }

}

fun Context.viewModelFactoryProvider() = (applicationContext as MyApp)

fun Fragment.viewModelFactoryProvider() = requireContext().viewModelFactoryProvider()