package com.zelyder.todoapp.di.components

import android.content.Context
import androidx.annotation.NonNull
import com.zelyder.todoapp.MyApp
import com.zelyder.todoapp.di.WorkerBindingModule
import com.zelyder.todoapp.di.modules.*
import com.zelyder.todoapp.presentation.edit_task.EditTaskFragment
import com.zelyder.todoapp.presentation.tasks_list.TasksListFragment
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Singleton

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        AppDataModule::class,
        NetworkModule::class,
        DataModule::class,
        AppBindModule::class,
        WorkerBindingModule::class,
        DispatcherModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        @NonNull
        fun create(
            @BindsInstance @NonNull context: Context
        ): AppComponent
    }

    @ExperimentalSerializationApi
    @ExperimentalCoroutinesApi
    fun inject(fragment: TasksListFragment)

    fun inject(fragment: EditTaskFragment)

    fun inject(myApp: MyApp)
}