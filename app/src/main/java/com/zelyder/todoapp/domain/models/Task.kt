package com.zelyder.todoapp.domain.models

data class Task(val text: String, val isDone: Boolean = false, val date: Long, val time: Long)
