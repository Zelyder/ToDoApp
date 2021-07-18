package com.zelyder.todoapp.data.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zelyder.todoapp.MyApp
import com.zelyder.todoapp.data.storage.DbContract
import com.zelyder.todoapp.data.storage.dao.DeletedTasksDao
import com.zelyder.todoapp.data.storage.dao.TasksDao
import com.zelyder.todoapp.data.storage.entities.DeletedTaskEntity
import com.zelyder.todoapp.data.storage.entities.TaskEntity


@Database(entities = [TaskEntity::class, DeletedTaskEntity::class], version = 2)
abstract class TasksDb: RoomDatabase() {

    abstract fun tasksDao(): TasksDao
    abstract fun deletedTasksDao(): DeletedTasksDao

    companion object {
        fun create(context: Context): TasksDb = Room.databaseBuilder(
            context,
            TasksDb::class.java,
            DbContract.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}