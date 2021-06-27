package com.zelyder.todoapp.domain.repositories


import com.zelyder.todoapp.data.initTasks
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.isToday

class TasksListRepositoryImpl: TasksListRepository {
    override fun getTasks(): List<Task> {
        return initTasks
    }

    override fun getCountTodayTasks(): Int {
        return initTasks.count { it.date?.let { date -> isToday(date) } ?: false }
    }
}