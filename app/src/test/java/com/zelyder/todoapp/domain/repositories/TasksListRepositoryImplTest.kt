package com.zelyder.todoapp.domain.repositories

import junit.framework.TestCase
import org.junit.Assert

class TasksListRepositoryImplTest : TestCase() {

    fun testGetCountTodayTasks() {
        val repository = TasksListRepositoryImpl()
        Assert.assertEquals(1, repository.getCountTodayTasks())
    }
}