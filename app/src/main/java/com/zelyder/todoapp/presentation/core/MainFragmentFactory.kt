package com.zelyder.todoapp.presentation.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.zelyder.todoapp.presentation.edit_task.EditTaskFragment
import com.zelyder.todoapp.presentation.tasks_list.TasksListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
class MainFragmentFactory @Inject constructor(private val viewModelFactory: ViewModelFactory): FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            TasksListFragment::class.java.name -> {
                TasksListFragment(viewModelFactory)
            }
            EditTaskFragment::class.java.name -> {
                EditTaskFragment(viewModelFactory)
            }
            else -> super.instantiate(classLoader, className)
        }
    }

}