package com.zelyder.todoapp.domain.models

import com.zelyder.todoapp.domain.enums.Importance

data class Task(
    val id: Long,
    val text: String,
    var isDone: Boolean = false,
    val importance: Importance = Importance.NONE,
    val dateTime: String? = null
)
