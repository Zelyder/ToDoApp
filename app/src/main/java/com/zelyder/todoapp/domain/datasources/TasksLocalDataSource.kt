package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.storage.entities.TaskEntity

interface TasksLocalDataSource {
    suspend fun getTasks(needFilter: Boolean = false):List<TaskEntity>
    suspend fun getTaskById(taskId: String):TaskEntity?
    suspend fun getCountOfDone():Int
    suspend fun saveTasks(tasks: List<TaskEntity>)
    suspend fun saveTask(task: TaskEntity)
    suspend fun setCheckTask(taskId: String, isDone: Boolean)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteTaskById(taskId: String)
    suspend fun deleteAllTasks()
}