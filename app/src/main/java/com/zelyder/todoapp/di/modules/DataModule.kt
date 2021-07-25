package com.zelyder.todoapp.di.modules

import android.content.Context
import com.zelyder.todoapp.data.network.TasksNetworkModule
import com.zelyder.todoapp.data.storage.db.TasksDb
import com.zelyder.todoapp.domain.datasources.*
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideTasksDb(applicationContext: Context) = TasksDb.create(applicationContext)

    @Provides
    @Singleton
    fun provideDeletedTasksDataSource(tasksDb: TasksDb): DeletedTasksDataSource =
        DeletedTasksDataSourceImpl(tasksDb.deletedTasksDao())

    @ExperimentalSerializationApi
    @Provides
    @Singleton
    fun provideTasksYandexDataSource(tasksNetworkModule: TasksNetworkModule): TasksYandexDataSource =
        TasksYandexDataSourceImpl(
            tasksNetworkModule.yandexApi()
        )
}