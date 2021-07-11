package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto

interface TasksYandexDataSource {
    suspend fun getTasks(): List<TaskDto>
    suspend fun sendTask(task: TaskDto)
    suspend fun updateTask(task: TaskDto): TaskDto
    suspend fun updateTasks(addAndDeleteDto: AddAndDeleteDto)
    suspend fun deleteTask(taskId: String)
}