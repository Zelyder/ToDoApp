package com.zelyder.todoapp.data.storage.dao

import androidx.room.*
import com.zelyder.todoapp.data.storage.DbContract
import com.zelyder.todoapp.data.storage.entities.TaskEntity

@Dao
interface TasksDao {

    @Query("select * from ${DbContract.Tasks.TABLE_NAME} where (:needFilter = 1 and" +
            " ${DbContract.Tasks.COLUMN_NAME_DONE} = 0) or :needFilter = 0 order by" +
            " ${DbContract.Tasks.COLUMN_NAME_DONE} asc, ${DbContract.Tasks.COLUMN_NAME_DEADLINE} is 0," +
            " ${DbContract.Tasks.COLUMN_NAME_DEADLINE} asc, ${DbContract.Tasks.COLUMN_NAME_IMPORTANCE} asc")
    suspend fun getAll(needFilter: Boolean = false): List<TaskEntity>

    @Query("select * from ${DbContract.Tasks.TABLE_NAME} where ${DbContract.Tasks.COLUMN_NAME_ID} = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity

    @Query ("select count(*) from ${DbContract.Tasks.TABLE_NAME} where ${DbContract.Tasks.COLUMN_NAME_DONE} = 1")
    suspend fun getCountOfDone(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("update ${DbContract.Tasks.TABLE_NAME} set ${DbContract.Tasks.COLUMN_NAME_DONE} = :isDone where ${DbContract.Tasks.COLUMN_NAME_ID} == :taskId")
    suspend fun setCheckTask(taskId: String, isDone: Boolean)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("delete from ${DbContract.Tasks.TABLE_NAME} where ${DbContract.Tasks.COLUMN_NAME_ID} = :taskId")
    suspend fun deleteById(taskId: String)

    @Query("delete from ${DbContract.Tasks.TABLE_NAME}")
    suspend fun deleteAll()

}