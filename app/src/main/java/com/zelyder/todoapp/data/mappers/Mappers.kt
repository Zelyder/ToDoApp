package com.zelyder.todoapp.data.mappers

import com.zelyder.todoapp.data.network.dto.TaskDto
import com.zelyder.todoapp.data.storage.entities.TaskEntity
import com.zelyder.todoapp.domain.enums.Importance
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
    deadline = if (date != null && date != "0") date!!.toDateInMillis() else 0L,
    createdAt = createdAt ?: Calendar.getInstance().timeInMillis,
    updatedAt = updatedAt ?: Calendar.getInstance().timeInMillis
)

fun TaskEntity.toDto(created: Long = createdAt) = TaskDto(
    id = id,
    text = text,
    importance = importance.type,
    done = isDone,
    deadline = deadline / 1000,
    createdAt = created / 1000,
    updatedAt = updatedAt / 1000
)

fun TaskDto.toEntity() = TaskEntity(
    id = id,
    text = text,
    importance = Importance.parse(importance),
    isDone = done,
    deadline = (deadline ?: 0) * 1000,
    createdAt = createdAt * 1000,
    updatedAt = updatedAt * 1000
)
