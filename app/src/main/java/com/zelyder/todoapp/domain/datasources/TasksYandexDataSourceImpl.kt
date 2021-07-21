package com.zelyder.todoapp.domain.datasources

import android.util.Log
import com.zelyder.todoapp.data.network.api.YandexApi
import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto
import com.zelyder.todoapp.domain.enums.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.lang.Exception
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class TasksYandexDataSourceImpl (private val yandexApi: YandexApi) : TasksYandexDataSource {
    override suspend fun getTasks(): NetworkResult<List<TaskDto>> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(yandexApi.getTasks())
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }
    }

    override suspend fun sendTask(task: TaskDto): NetworkResult<TaskDto> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(yandexApi.sendTask(task))
            }catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }
    }

    override suspend fun updateTask(task: TaskDto): NetworkResult<TaskDto> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(yandexApi.updateTask(task, task.id))
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }
    }

    override suspend fun updateTasks(addAndDeleteDto: AddAndDeleteDto): NetworkResult<List<TaskDto>> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(yandexApi.updateTasks(addAndDeleteDto))
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }
    }

    override suspend fun deleteTask(taskId: String): NetworkResult<TaskDto> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(yandexApi.deleteTask(taskId))
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }
    }
}