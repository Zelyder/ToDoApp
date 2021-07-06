package com.zelyder.todoapp.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import com.zelyder.todoapp.presentation.edit_task.EditTaskViewModel
import com.zelyder.todoapp.presentation.tasks_list.TasksListViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val tasksListRepository: TasksListRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  = when(modelClass){
        TasksListViewModel::class.java -> TasksListViewModel(tasksListRepository)
        EditTaskViewModel::class.java -> EditTaskViewModel()
        else ->throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T
}