package com.zelyder.todoapp.di.modules

import com.zelyder.todoapp.domain.datasources.TasksLocalDataSource
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSourceImpl
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.domain.repositories.TasksListRepositoryImpl
import com.zelyder.todoapp.presentation.core.NetworkStatusTracker
import com.zelyder.todoapp.presentation.core.NetworkStatusTrackerImpl
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
interface AppBindModule {

    @ExperimentalCoroutinesApi
    @Binds
    fun bindTasksListRepositoryImpl(
        tasksListRepositoryImpl: TasksListRepositoryImpl
    ) : TasksListRepository

    @Binds
    fun bindTasksLocalDataSourceImpl(
        tasksListDataSourceImpl: TasksLocalDataSourceImpl
    ): TasksLocalDataSource

    @ExperimentalCoroutinesApi
    @Binds
    fun bindNetworkStatusTrackerImpl(
        networkStatusTrackerImpl: NetworkStatusTrackerImpl
    ): NetworkStatusTracker

}