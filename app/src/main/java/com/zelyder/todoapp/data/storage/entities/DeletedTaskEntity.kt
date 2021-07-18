package com.zelyder.todoapp.data.storage.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zelyder.todoapp.data.storage.DbContract

@Entity(tableName = DbContract.DeletedTasks.TABLE_NAME)
data class DeletedTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = DbContract.DeletedTasks.COLUMN_NAME_ID)
    val id: String
)
