package com.zelyder.todoapp.data.storage.dao

import androidx.room.*
import com.zelyder.todoapp.data.storage.DbContract
import com.zelyder.todoapp.data.storage.entities.DeletedTaskEntity

@Dao
interface DeletedTasksDao {

    @Query("select * from ${DbContract.DeletedTasks.TABLE_NAME}")
    suspend fun getAll(): List<DeletedTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DeletedTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: DeletedTaskEntity)

    @Delete
    suspend fun delete(task: DeletedTaskEntity)

    @Query("delete from ${DbContract.DeletedTasks.TABLE_NAME}")
    suspend fun deleteAll()
}