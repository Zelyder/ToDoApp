package com.zelyder.todoapp.domain.repositories

import com.zelyder.todoapp.domain.models.Task

interface TasksListRepository {
    suspend fun checkInternetAndSync()
    suspend fun getTasks(needFilter: Boolean = false): List<Task>
    suspend fun getCountOfDone(): Int
    suspend fun addTask(task: Task)
    suspend fun setCheckTask(taskId: String, isDone: Boolean)
    suspend fun editTask(task: Task)
    suspend fun deleteTaskById(taskId: String)
    suspend fun getCountTodayTasks() : Int
}