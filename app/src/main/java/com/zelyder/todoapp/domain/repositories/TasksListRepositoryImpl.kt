package com.zelyder.todoapp.domain.repositories

import com.zelyder.todoapp.data.DataSource
import com.zelyder.todoapp.domain.models.Task

class TasksListRepositoryImpl: TasksListRepository {
    override fun getTasks(): List<Task> {
        return DataSource.getData()
    }
}