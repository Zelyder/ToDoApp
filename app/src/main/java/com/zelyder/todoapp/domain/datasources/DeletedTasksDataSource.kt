package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.storage.entities.DeletedTaskEntity

interface DeletedTasksDataSource {
    suspend fun getAll(): List<DeletedTaskEntity>
    suspend fun addTask(taskEntity: DeletedTaskEntity)
    suspend fun addAll(tasksEntity: List<DeletedTaskEntity>)
    suspend fun deleteTask(taskEntity: DeletedTaskEntity)
    suspend fun clear()
}