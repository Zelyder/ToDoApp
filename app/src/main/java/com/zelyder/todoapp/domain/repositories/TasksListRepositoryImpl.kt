package com.zelyder.todoapp.domain.repositories


import android.util.Log
import com.zelyder.todoapp.data.mappers.toDto
import com.zelyder.todoapp.data.mappers.toEntity
import com.zelyder.todoapp.data.mappers.toTask
import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto
import com.zelyder.todoapp.di.modules.IoDispatcher
import com.zelyder.todoapp.domain.datasources.DeletedTasksDataSource
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSource
import com.zelyder.todoapp.domain.datasources.TasksYandexDataSource
import com.zelyder.todoapp.domain.enums.NetworkResult
import com.zelyder.todoapp.domain.enums.NetworkStatus
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.NetworkStatusTracker
import com.zelyder.todoapp.presentation.core.NetworkStatusTrackerImpl
import com.zelyder.todoapp.presentation.core.isToday
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TasksListRepositoryImpl @Inject constructor(
    private val tasksLocalDataSource: TasksLocalDataSource,
    private val deletedTasksDataSource: DeletedTasksDataSource,
    private val yandexDataSource: TasksYandexDataSource,
    private val networkTracker: NetworkStatusTracker,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : TasksListRepository {

    private var isConnected = false

    override suspend fun checkInternetAndSync() = withContext(dispatcher) {
        networkTracker.getNetworkStatus()
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

    override suspend fun getTasks(needFilter: Boolean): List<Task> = withContext(dispatcher) {
        tasksLocalDataSource.getTasks(needFilter).map { it.toTask() }
    }

    override suspend fun getCountOfDone(): Int = withContext(dispatcher) {
        tasksLocalDataSource.getCountOfDone()
    }

    override suspend fun addTask(task: Task): Unit = withContext(dispatcher) {

        val taskEntity = if (isConnected) task.toEntity() else task.toEntity(createdAt = 0L)
        tasksLocalDataSource.saveTask(taskEntity)
        if (isConnected) {
            yandexDataSource.sendTask(taskEntity.toDto())
        }
    }

    override suspend fun setCheckTask(taskId: String, isDone: Boolean) =
        withContext(dispatcher) {
            tasksLocalDataSource.setCheckTask(taskId, isDone)
            if (isConnected) {
                tasksLocalDataSource.getTaskById(taskId)?.toDto()
                    ?.let { yandexDataSource.updateTask(it) }
            }
        }

    override suspend fun editTask(task: Task) = withContext(dispatcher) {
        val oldTask = tasksLocalDataSource.getTaskById(task.id)
        if (oldTask != null) {
            val newTask = task.toEntity(createdAt = oldTask.createdAt)
            tasksLocalDataSource.updateTask(newTask)
            if (isConnected) {
                yandexDataSource.updateTask(newTask.toDto())
            }
        }
    }

    override suspend fun deleteTaskById(taskId: String) = withContext(dispatcher) {
        tasksLocalDataSource.deleteTaskById(taskId)
        if (isConnected) {
            yandexDataSource.deleteTask(taskId)
        }
    }

    override suspend fun getCountTodayTasks(): Int = withContext(dispatcher) {
        tasksLocalDataSource.getTasks().map { it.toTask() }
            .count { it.date?.let { date -> isToday(date) } ?: false }
    }

    private suspend fun sync() = withContext(dispatcher) {
        when (val result = yandexDataSource.getTasks()) {
            is NetworkResult.Success -> {
                val localTasks = tasksLocalDataSource.getTasks()
                val cloudTasks = result.data
                val localTasksSet = localTasks.map { it.id }.toSet()
                val cloudTasksSet = cloudTasks.map { it.id }.toSet()

                val newLocalTasks = localTasks.filter { it.createdAt == 0L }

                val common = localTasksSet.intersect(cloudTasksSet)

                val delList = deletedTasksDataSource.getAll().map { it.id }

                val updateCloudList: MutableList<TaskDto> = mutableListOf()

                if (common.isNotEmpty()) {
                    val local = localTasks.associateBy { it.id }
                    val cloud = cloudTasks.associateBy { it.id }
                    for (id in common) {
                        if (local.getValue(id).toDto().updatedAt > cloud.getValue(id).updatedAt) {
                            updateCloudList.add(local.getValue(id).toDto())
                        }
                    }
                }
                val addAndDeleteDto = AddAndDeleteDto(
                    delList,
                    newLocalTasks.map {
                        it.toDto(created = it.updatedAt)
                    } + updateCloudList
                )
                val resultNewTasks = yandexDataSource.updateTasks(addAndDeleteDto)
                if (resultNewTasks is NetworkResult.Success) {
                    tasksLocalDataSource.deleteAllTasks()
                    deletedTasksDataSource.clear()
                    tasksLocalDataSource.saveTasks(resultNewTasks.data.map { it.toEntity() })
                } else {
                    Log.d(javaClass.simpleName, "resultNewTasks Error")
                }
            }
            is NetworkResult.Error -> Log.d(javaClass.simpleName, "NetworkResult.Error")
        }
    }
}