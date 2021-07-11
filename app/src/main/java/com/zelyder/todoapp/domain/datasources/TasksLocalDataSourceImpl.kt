package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.storage.db.TasksDb
import com.zelyder.todoapp.data.storage.entities.DeletedTaskEntity
import com.zelyder.todoapp.data.storage.entities.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksLocalDataSourceImpl(private val tasksDb: TasksDb): TasksLocalDataSource {
    override suspend fun getTasks(needFilter: Boolean): List<TaskEntity>  = withContext(Dispatchers.IO){
        tasksDb.tasksDao().getAll(needFilter)
    }

    override suspend fun getTaskById(taskId: String): TaskEntity = withContext(Dispatchers.IO){
        tasksDb.tasksDao().getTaskById(taskId)
    }

    override suspend fun getCountOfDone(): Int = withContext(Dispatchers.IO){
        tasksDb.tasksDao().getCountOfDone()
    }

    override suspend fun saveTasks(tasks: List<TaskEntity>) = withContext(Dispatchers.IO){
        tasksDb.tasksDao().insertAll(tasks)
    }

    override suspend fun saveTask(task: TaskEntity) = withContext(Dispatchers.IO){
        tasksDb.tasksDao().insert(task)
    }

    override suspend fun setCheckTask(taskId: String, isDone: Boolean) {
        tasksDb.tasksDao().setCheckTask(taskId, isDone)
    }

    override suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO){
        tasksDb.tasksDao().update(task)
    }

    override suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO){
        tasksDb.tasksDao().delete(task)
        tasksDb.deletedTasksDao().insert(DeletedTaskEntity(task.id))
    }

    override suspend fun deleteTaskById(taskId: String) = withContext(Dispatchers.IO){
        tasksDb.tasksDao().deleteById(taskId)
        tasksDb.deletedTasksDao().insert(DeletedTaskEntity(taskId))
    }

    override suspend fun deleteAllTasks() = withContext(Dispatchers.IO){
        val temp = tasksDb.tasksDao().getAll()
        tasksDb.tasksDao().deleteAll()
        tasksDb.deletedTasksDao().insertAll(temp.map { DeletedTaskEntity(it.id) })
    }
}