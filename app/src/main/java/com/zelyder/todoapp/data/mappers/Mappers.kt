package com.zelyder.todoapp.data.mappers

import com.zelyder.todoapp.data.storage.entities.TaskEntity
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.toDateInMillis
import com.zelyder.todoapp.presentation.core.toDate
import java.util.*

fun TaskEntity.toTask() = Task(
    id = id,
    text = text,
    isDone = isDone,
    importance = importance,
    date = if (deadline == 0L) null else deadline.toDate()
)

fun Task.toEntity(createdAt: Long? = null, updatedAt: Long? = null) = TaskEntity(
    id = id,
    text = text,
    importance = importance,
    isDone = isDone,
    deadline = date?.toDateInMillis() ?: 0L,
    createdAt = createdAt ?: Calendar.getInstance().timeInMillis,
    updatedAt = updatedAt ?: Calendar.getInstance().timeInMillis
)