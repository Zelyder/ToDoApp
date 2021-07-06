package com.zelyder.todoapp.domain.repositories

import com.zelyder.todoapp.domain.models.Task

interface TasksListRepository {
    fun getTasks(): List<Task>
    fun getCountTodayTasks() : Int
}