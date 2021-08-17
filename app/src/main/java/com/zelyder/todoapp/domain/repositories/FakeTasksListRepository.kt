package com.zelyder.todoapp.domain.repositories

import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task

class FakeTasksListRepository(initTasks: List<Task>? = null, private val today: String = "24 июля 2021"): TasksListRepository {

    val tasks = initTasks?.toMutableList()
        ?: mutableListOf(
            Task("f856521e-0208-4d7c-84fd-ec22bc39c886", "test 1", false, Importance.NONE),
            Task("8b9a796c-e6e7-4543-93d7-4fda251cef30", "test 2", false, Importance.HIGH),
            Task("eebc4ce8-4741-420f-b302-a721afe6db25", "test 3", true, Importance.LOW),
            Task("27987619-7c11-461a-8cb0-7b3c87d14b47", "test 4", true, Importance.NONE),
        )

    override suspend fun checkInternetAndSync() {

    }

    override suspend fun getTasks(needFilter: Boolean): List<Task> = tasks

    override suspend fun getCountOfDone(): Int = tasks.count { it.isDone }

    override suspend fun addTask(task: Task) {
        tasks.add(task)
    }

    override suspend fun setCheckTask(taskId: String, isDone: Boolean) {
        tasks.find { it.id == taskId }?.isDone =isDone
    }

    override suspend fun editTask(task: Task) {
        tasks.find { it.id == task.id }?.apply {
            text = task.text
            isDone = task.isDone
            importance = task.importance
            date = task.date
        }
    }

    override suspend fun deleteTaskById(taskId: String) {
        tasks.removeAll { it.id == taskId }
    }

    override suspend fun getCountTodayTasks(): Int = tasks.count { it.date == today }
}