package com.zelyder.todoapp.data.storage.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zelyder.todoapp.data.storage.DbContract
import com.zelyder.todoapp.domain.enums.Importance
import java.util.*


@Entity(tableName = DbContract.Tasks.TABLE_NAME)
data class TaskEntity (
    @PrimaryKey
    @ColumnInfo(name = DbContract.Tasks.COLUMN_NAME_ID)
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = DbContract.Tasks.COLUMN_NAME_TEXT)
    val text: String,
    @ColumnInfo(name = DbContract.Tasks.COLUMN_NAME_IMPORTANCE)
    val importance: Importance = Importance.NONE,
    @ColumnInfo(name = DbContract.Tasks.COLUMN_NAME_DONE)
    val isDone: Boolean = false,
    @ColumnInfo(name = DbContract.Tasks.COLUMN_NAME_DEADLINE)
    val deadline: Long = 0L,
    @ColumnInfo(name = DbContract.Tasks.COLUMN_NAME_CREATED_AT)
    val createdAt: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo(name = DbContract.Tasks.COLUMN_NAME_UPDATED_AT)
    val updatedAt: Long = Calendar.getInstance().timeInMillis,
)
