package com.zelyder.todoapp.domain.repositories


import android.util.Log
import com.zelyder.todoapp.data.mappers.toDto
import com.zelyder.todoapp.data.mappers.toEntity
import com.zelyder.todoapp.data.mappers.toTask
import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto
import com.zelyder.todoapp.data.storage.entities.TaskEntity
import com.zelyder.todoapp.domain.datasources.DeletedTasksDataSource
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSource
import com.zelyder.todoapp.domain.datasources.TasksYandexDataSource
import com.zelyder.todoapp.domain.enums.NetworkResult
import com.zelyder.todoapp.domain.enums.NetworkStatus
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.NetworkStatusTracker
import com.zelyder.todoapp.presentation.core.isToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class TasksListRepositoryImpl(
    private val tasksLocalDataSource: TasksLocalDataSource,
    private val deletedTasksDataSource: DeletedTasksDataSource,
    private val yandexDataSource: TasksYandexDataSource,
    private val networkTracker: NetworkStatusTracker
) : TasksListRepository {

    var isConnected = false

    override suspend fun checkInternetAndSync() {
        networkTracker.networkStatus
            .collectLatest {
                isConnected = when (it) {
                    NetworkStatus.Unavailable -> false
                    NetworkStatus.Available -> {
                        sync()
                        true
                    }
                }
            }
    }

    override suspend fun getTasks(needFilter: Boolean): List<Task> {
        return tasksLocalDataSource.getTasks(needFilter).map { it.toTask() }
    }

    override suspend fun getCountOfDone(): Int = tasksLocalDataSource.getCountOfDone()

    override suspend fun addTask(task: Task): Unit = withContext(Dispatchers.IO) {
        val taskEntity = task.toEntity()
        tasksLocalDataSource.saveTask(taskEntity)
        if (isConnected) {
            yandexDataSource.sendTask(taskEntity.toDto())
        }
    }

    override suspend fun setCheckTask(taskId: String, isDone: Boolean) {
        tasksLocalDataSource.setCheckTask(taskId, isDone)
        if (isConnected) {
            tasksLocalDataSource.getTaskById(taskId)?.toDto()
                ?.let { yandexDataSource.updateTask(it) }
        }
    }

    override suspend fun editTask(task: Task) {
        val oldTask = tasksLocalDataSource.getTaskById(task.id)
        if (oldTask != null) {
            val newTask = task.toEntity(createdAt = oldTask.createdAt)
            tasksLocalDataSource.updateTask(newTask)
            if (isConnected) {
                yandexDataSource.updateTask(newTask.toDto())
            }
        }
    }

    override suspend fun deleteTaskById(taskId: String) {
        tasksLocalDataSource.deleteTaskById(taskId)
        if (isConnected) {
            yandexDataSource.deleteTask(taskId)
        }
    }

    override suspend fun getCountTodayTasks(): Int {
        return tasksLocalDataSource.getTasks().map { it.toTask() }
            .count { it.date?.let { date -> isToday(date) } ?: false }
    }

    suspend fun sync() {
        when (val result = yandexDataSource.getTasks()) {
            is NetworkResult.Success -> {
                val localTasks = tasksLocalDataSource.getTasks()
                val cloudTasks = result.data
                val localTasksSet = localTasks.map { it.id }.toSet()
                val cloudTasksSet = cloudTasks.map { it.id }.toSet()

                val diff = localTasksSet - cloudTasksSet
                val common = localTasksSet.intersect(cloudTasksSet)

                val delList = deletedTasksDataSource.getAll().map { it.id }.toSet()

                val newLocalTasks = diff - delList

                val updateCloudList: MutableList<TaskDto> = mutableListOf()
                val updateLocalList: MutableList<TaskEntity> = mutableListOf()

                if (common.isNotEmpty()) {
                    val localCommon = localTasks.filter { it.id in common }.sortedBy { it.id }
                    val cloudCommon = cloudTasks.filter { it.id in common }.sortedBy { it.id }
                    for (i in common.indices) {
                        if (localCommon[i].updatedAt > cloudCommon[i].updatedAt) {
                            updateCloudList.add(localCommon[i].toDto())
                        } else if (localCommon[i].updatedAt < cloudCommon[i].updatedAt) {
                            updateLocalList.add(cloudCommon[i].toEntity())
                        }
                    }
                }
                val addAndDeleteDto = AddAndDeleteDto(
                    delList.toList(),
                    localTasks.filter { it.id in newLocalTasks }.map { it.toDto() } + updateCloudList
                )
                val resultNewTasks = yandexDataSource.updateTasks(addAndDeleteDto)
                if (resultNewTasks is NetworkResult.Success) {
                    tasksLocalDataSource.deleteAllTasks()
                    deletedTasksDataSource.clear()
                    tasksLocalDataSource.saveTasks(resultNewTasks.data.map { it.toEntity() })
                    updateLocalList.onEach { tasksLocalDataSource.updateTask(it) }
                } else {
                    Log.d(javaClass.simpleName, "resultNewTasks Error")
                }
            }
            is NetworkResult.Error -> Log.d(javaClass.simpleName, "NetworkResult.Error")
        }
    }
}