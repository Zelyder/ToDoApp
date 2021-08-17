package com.zelyder.todoapp.di.modules

import android.content.Context
import com.zelyder.todoapp.data.network.TasksNetworkModule
import com.zelyder.todoapp.data.storage.db.TasksDb
import com.zelyder.todoapp.di.components.AppScope
import com.zelyder.todoapp.domain.datasources.DeletedTasksDataSource
import com.zelyder.todoapp.domain.datasources.DeletedTasksDataSourceImpl
import com.zelyder.todoapp.domain.datasources.TasksYandexDataSource
import com.zelyder.todoapp.domain.datasources.TasksYandexDataSourceImpl
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi

@Module
class DataModule {

    @Provides
    @AppScope
    fun provideTasksDb(applicationContext: Context) = TasksDb.create(applicationContext)

    @Provides
    @AppScope
    fun provideDeletedTasksDataSource(tasksDb: TasksDb): DeletedTasksDataSource =
        DeletedTasksDataSourceImpl(tasksDb.deletedTasksDao())

    @ExperimentalSerializationApi
    @Provides
    @AppScope
    fun provideTasksYandexDataSource(tasksNetworkModule: TasksNetworkModule): TasksYandexDataSource =
        TasksYandexDataSourceImpl(
            tasksNetworkModule.yandexApi()
        )
}