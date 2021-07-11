package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.network.api.YandexApi
import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksYandexDataSourceImpl(private val yandexApi: YandexApi): TasksYandexDataSource {
    override suspend fun getTasks(): List<TaskDto> = withContext(Dispatchers.IO){
        yandexApi.getTasks()
    }

    override suspend fun sendTask(task: TaskDto) = withContext(Dispatchers.IO){
        yandexApi.sendTask(task)
    }

    override suspend fun updateTask(task: TaskDto): TaskDto = withContext(Dispatchers.IO){
        yandexApi.updateTask(task, task.id)
    }

    override suspend fun updateTasks(addAndDeleteDto: AddAndDeleteDto) = withContext(Dispatchers.IO){
        yandexApi.updateTasks(addAndDeleteDto)
    }

    override suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO){
        yandexApi.deleteTask(taskId)
    }
}