package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto
import com.zelyder.todoapp.domain.enums.NetworkResult

interface TasksYandexDataSource {
    suspend fun getTasks(): NetworkResult<List<TaskDto>>
    suspend fun sendTask(task: TaskDto): NetworkResult<TaskDto>
    suspend fun updateTask(task: TaskDto): NetworkResult<TaskDto>
    suspend fun updateTasks(addAndDeleteDto: AddAndDeleteDto): NetworkResult<List<TaskDto>>
    suspend fun deleteTask(taskId: String): NetworkResult<TaskDto>
}