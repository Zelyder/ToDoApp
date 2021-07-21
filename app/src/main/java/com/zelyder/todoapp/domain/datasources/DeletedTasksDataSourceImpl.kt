package com.zelyder.todoapp.domain.datasources

import com.zelyder.todoapp.data.storage.dao.DeletedTasksDao
import com.zelyder.todoapp.data.storage.entities.DeletedTaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeletedTasksDataSourceImpl (private val deletedTasksDao: DeletedTasksDao): DeletedTasksDataSource {
    override suspend fun getAll(): List<DeletedTaskEntity> = withContext(Dispatchers.IO){
        deletedTasksDao.getAll()
    }

    override suspend fun addTask(taskEntity: DeletedTaskEntity) = withContext(Dispatchers.IO){
        deletedTasksDao.insert(taskEntity)
    }

    override suspend fun addAll(tasksEntity: List<DeletedTaskEntity>) = withContext(Dispatchers.IO){
        deletedTasksDao.insertAll(tasksEntity)
    }

    override suspend fun deleteTask(taskEntity: DeletedTaskEntity) = withContext(Dispatchers.IO){
        deletedTasksDao.delete(taskEntity)
    }

    override suspend fun clear() = withContext(Dispatchers.IO){
        deletedTasksDao.deleteAll()
    }
}