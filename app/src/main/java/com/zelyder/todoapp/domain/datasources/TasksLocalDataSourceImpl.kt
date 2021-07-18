package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.storage.db.TasksDb
import com.zelyder.todoapp.data.storage.entities.DeletedTaskEntity
import com.zelyder.todoapp.data.storage.entities.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksLocalDataSourceImpl(private val tasksDb: TasksDb): TasksLocalDataSource {
    override suspend fun getTasks(needFilter: Boolean): List<TaskEntity> {
        return tasksDb.tasksDao().getAll(needFilter)
    }

    override suspend fun getTaskById(taskId: String): TaskEntity {
        return tasksDb.tasksDao().getTaskById(taskId)
    }

    override suspend fun getCountOfDone(): Int {
        return tasksDb.tasksDao().getCountOfDone()
    }

    override suspend fun saveTasks(tasks: List<TaskEntity>) {
        tasksDb.tasksDao().insertAll(tasks)
    }

    override suspend fun saveTask(task: TaskEntity) {
        tasksDb.tasksDao().insert(task)
    }

    override suspend fun setCheckTask(taskId: String, isDone: Boolean)  {
        tasksDb.tasksDao().setCheckTask(taskId, isDone)
    }

    override suspend fun updateTask(task: TaskEntity) {
        tasksDb.tasksDao().update(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        tasksDb.tasksDao().delete(task)
        tasksDb.deletedTasksDao().insert(DeletedTaskEntity(task.id))
    }

    override suspend fun deleteTaskById(taskId: String) {
        tasksDb.tasksDao().deleteById(taskId)
        tasksDb.deletedTasksDao().insert(DeletedTaskEntity(taskId))
    }

    override suspend fun deleteAllTasks() {
        tasksDb.tasksDao().deleteAll()
    }
}