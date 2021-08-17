package com.zelyder.todoapp.presentation.tasks_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelyder.todoapp.di.components.AppScope
import com.zelyder.todoapp.di.modules.MainDispatcher
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class TasksListViewModel @Inject constructor(
    private val tasksListRepository: TasksListRepository,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    private val _isHidden = MutableLiveData(true)
    private val _doneCount = MutableLiveData<Int>()

    val tasks: LiveData<List<Task>> get() = _tasks
    val isHidden: LiveData<Boolean> get() = _isHidden
    val doneCount: LiveData<Int> get() = _doneCount


    fun updateList() {
        viewModelScope.launch(dispatcher) {
            _tasks.value =
                if (_isHidden.value == false) tasksListRepository.getTasks() else tasksListRepository.getTasks()
                    .filter { !it.isDone }
            _doneCount.value = tasksListRepository.getCountOfDone()
        }
    }

    fun toggleVisibility() {
        if (_isHidden.value == true) {
            _isHidden.value = false
        } else if (_isHidden.value == false) {
            _isHidden.value = true
        }
        updateList()
    }

    fun checkTask(task: Task) {
        viewModelScope.launch(dispatcher) {
            tasksListRepository.editTask(task)
            _doneCount.value = tasksListRepository.getCountOfDone()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(dispatcher) {
            tasksListRepository.deleteTaskById(task.id)
            val newList = tasks.value?.toMutableList()
            newList?.remove(task)
            newList?.let { _tasks.value = it }

        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch(dispatcher) {
            tasksListRepository.addTask(task)
            val newList = tasks.value?.toMutableList()
            newList?.add(task)
            newList?.let { _tasks.value = it }

        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch(dispatcher) {
            tasksListRepository.editTask(task)
            updateList()
        }
    }

    fun sync() {
        viewModelScope.launch(dispatcher) {
            tasksListRepository.checkInternetAndSync()
            updateList()
        }
    }


}