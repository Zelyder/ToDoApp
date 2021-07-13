package com.zelyder.todoapp.presentation.tasks_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import kotlinx.coroutines.launch

class TasksListViewModel(private val tasksListRepository: TasksListRepository) : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    private val _isHided = MutableLiveData(true)
    private val _doneCount = MutableLiveData<Int>()

    val tasks: LiveData<List<Task>> get() = _tasks
    val isHided: LiveData<Boolean> get() = _isHided
    val doneCount: LiveData<Int> get() = _doneCount


    fun updateList() {
        viewModelScope.launch {
            _tasks.value =
                if (_isHided.value == false) tasksListRepository.getTasks() else tasksListRepository.getTasks()
                    .filter { !it.isDone }
            _doneCount.value = tasksListRepository.getCountOfDone()
        }
    }

    fun toggleVisibility() {
        if (_isHided.value == true) {
            _isHided.value = false
        } else if (_isHided.value == false) {
            _isHided.value = true
        }
        updateList()
    }

    fun checkTask(task: Task) {
        viewModelScope.launch {
            tasksListRepository.setCheckTask(taskId = task.id, task.isDone)
            _doneCount.value = tasksListRepository.getCountOfDone()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksListRepository.deleteTaskById(task.id)
            val newList = tasks.value?.toMutableList()
            newList?.remove(task)
            _tasks.value = newList
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            tasksListRepository.addTask(task)
            val newList = tasks.value?.toMutableList()
            newList?.add(task)
            _tasks.value = newList
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            tasksListRepository.editTask(task)
            updateList()
        }
    }

    fun sync() {
        viewModelScope.launch {
            tasksListRepository.checkInternetAndSync()
            updateList()
        }
    }


}