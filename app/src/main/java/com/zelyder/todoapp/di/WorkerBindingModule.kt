package com.zelyder.todoapp.di

import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import com.zelyder.todoapp.presentation.background.ChildWorkerFactory
import com.zelyder.todoapp.presentation.background.MyWorkerFactory
import com.zelyder.todoapp.presentation.background.ReminderWorker
import com.zelyder.todoapp.presentation.background.UpdateWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@Module
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(ReminderWorker::class)
    fun bindReminderWorker(factory: ReminderWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(UpdateWorker::class)
    fun bindUpdateWorker(factory: UpdateWorker.Factory): ChildWorkerFactory

    @Binds
    fun bindMyWorkerFactory(factory: MyWorkerFactory): WorkerFactory
}