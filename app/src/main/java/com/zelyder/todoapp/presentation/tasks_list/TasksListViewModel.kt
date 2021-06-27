package com.zelyder.todoapp.presentation.tasks_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.domain.repositories.TasksListRepository

class TasksListViewModel(private val tasksListRepository: TasksListRepository) : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    private val _isHided = MutableLiveData(true)
    private val _checkedTasks = MutableLiveData<List<Task>>()
    private val _uncheckedTasks = MutableLiveData<List<Task>>()
    private val _doneCount = MutableLiveData<Int>()

    val tasks: LiveData<List<Task>> get() = _tasks
    val isHided: LiveData<Boolean> get() = _isHided
    val doneCount: LiveData<Int> get() = _doneCount

    fun updateList() {
        if (_tasks.value.isNullOrEmpty()) {
            val data = tasksListRepository.getTasks()
            _uncheckedTasks.value = data.filter { !it.isDone }
            _checkedTasks.value = data.filter { it.isDone }
            _doneCount.value = _checkedTasks.value?.size
            _tasks.value = _uncheckedTasks.value
        }
    }

    fun toggleVisibility() {
        if (_isHided.value == true) {
            _isHided.value = false
        } else if (_isHided.value == false) {
            _isHided.value = true
        }

        if (_isHided.value == true) {
            _tasks.value = _uncheckedTasks.value.orEmpty() + _checkedTasks.value.orEmpty()
        } else {
            _tasks.value = _uncheckedTasks.value
        }
    }

    fun checkTask(task: Task) {
        if (task.isDone) {
            val newCheckedList = _checkedTasks.value?.toMutableList()
            newCheckedList?.add(task)
            _checkedTasks.value = newCheckedList

            val newUncheckedList = _uncheckedTasks.value?.toMutableList()
            newUncheckedList?.remove(task)
            _uncheckedTasks.value = newUncheckedList

            _doneCount.value = _checkedTasks.value?.size
        } else {
            val newCheckedList = _checkedTasks.value?.toMutableList()
            newCheckedList?.remove(task)
            _checkedTasks.value = newCheckedList

            val newUncheckedList = _uncheckedTasks.value?.toMutableList()
            newUncheckedList?.add(task)
            _uncheckedTasks.value = newUncheckedList

            _doneCount.value = _checkedTasks.value?.size
        }
    }

    fun deleteTask(task: Task) {
        if (task.isDone) {
            val newCheckedList = _checkedTasks.value?.toMutableList()
            newCheckedList?.remove(task)
            _checkedTasks.value = newCheckedList

            _doneCount.value = _checkedTasks.value?.size
        } else {
            val newUncheckedList = _uncheckedTasks.value?.toMutableList()
            newUncheckedList?.remove(task)
            _uncheckedTasks.value = newUncheckedList
        }
    }

    fun addTask(task: Task) {
        val newUncheckedList = _uncheckedTasks.value?.toMutableList()
        newUncheckedList?.add(task)
        _uncheckedTasks.value = newUncheckedList

    }

    fun editTask(task: Task) {
        if (task.isDone) {
            val newCheckedList = _checkedTasks.value?.toMutableList()
            newCheckedList?.find { it.id == task.id }?.apply {
                text = task.text
                importance = task.importance
                dateTime = task.dateTime
            }
            _checkedTasks.value = newCheckedList
        } else {
            val newUncheckedList = _uncheckedTasks.value?.toMutableList()
            newUncheckedList?.find { it.id == task.id }?.apply {
                text = task.text
                importance = task.importance
                dateTime = task.dateTime
            }
            _uncheckedTasks.value = newUncheckedList
        }
    }


}