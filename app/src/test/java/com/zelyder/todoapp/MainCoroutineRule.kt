package com.zelyder.todoapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@ExperimentalCoroutinesApi
class MainCoroutineRule: TestRule {
    val testDispatcher = TestCoroutineDispatcher()
    override fun apply(base: Statement?, description: Description?): Statement  = object: Statement() {
        override fun evaluate() {
            Dispatchers.setMain(testDispatcher)
            try {
                base?.evaluate()
            } finally {
                Dispatchers.resetMain()
                testDispatcher.cleanupTestCoroutines()
            }
        }

    }
}