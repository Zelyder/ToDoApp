package com.zelyder.todoapp.di

import com.zelyder.todoapp.domain.datasources.TasksLocalDataSource
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSourceImpl
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.domain.repositories.TasksListRepositoryImpl
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

}