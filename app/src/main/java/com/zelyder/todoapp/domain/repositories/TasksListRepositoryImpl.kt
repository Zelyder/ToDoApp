package com.zelyder.todoapp.domain.repositories


import android.util.Log
import com.zelyder.todoapp.data.initTasks
import com.zelyder.todoapp.data.mappers.toEntity
import com.zelyder.todoapp.data.mappers.toTask
import com.zelyder.todoapp.data.network.TasksNetworkClient
import com.zelyder.todoapp.data.network.api.YandexApi
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSource
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.isToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksListRepositoryImpl(private val tasksLocalDataSource: TasksLocalDataSource, private val yandexApi: YandexApi): TasksListRepository {
    override suspend fun getTasks(needFilter: Boolean): List<Task> {
        return tasksLocalDataSource.getTasks(needFilter).map { it.toTask() }
    }

    override suspend fun getCountOfDone(): Int = tasksLocalDataSource.getCountOfDone()

    override suspend fun addTask(task: Task): Unit = withContext(Dispatchers.IO){
        tasksLocalDataSource.saveTask(task.toEntity())
    }

    override suspend fun setCheckTask(taskId: String, isDone: Boolean) {
        tasksLocalDataSource.setCheckTask(taskId, isDone)
    }

    override suspend fun editTask(task: Task) {
        val oldTask = tasksLocalDataSource.getTaskById(task.id)
        if (oldTask != null) {
            tasksLocalDataSource.updateTask(task.toEntity(createdAt = oldTask.createdAt))
        }
    }

    override suspend fun deleteTaskById(taskId: String) {
        tasksLocalDataSource.deleteTaskById(taskId)
    }

    override suspend fun getCountTodayTasks(): Int {
        return tasksLocalDataSource.getTasks().map { it.toTask() }.count { it.date?.let { date -> isToday(date) } ?: false }
    }
}