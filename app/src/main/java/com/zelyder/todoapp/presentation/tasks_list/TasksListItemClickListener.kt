package com.zelyder.todoapp.presentation.tasks_list

import com.zelyder.todoapp.domain.models.Task

interface TasksListItemClickListener {
    fun onCheck(task: Task)
    fun onItemClick(task: Task)
    fun onDelete(task: Task)

}