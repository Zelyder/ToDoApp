package com.zelyder.todoapp.presentation.tasks_list

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.zelyder.todoapp.R
import com.zelyder.todoapp.domain.repositories.FakeTasksListRepository
import com.zelyder.todoapp.presentation.core.MainFragmentFactory
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
class TasksListFragmentTest {

    private lateinit var scenario: FragmentScenario<TasksListFragment>
    private lateinit var repository: FakeTasksListRepository

    @Before
    fun setup() {
        val bundle = Bundle()
        repository = FakeTasksListRepository()
        val viewModelFactory = ViewModelFactory(repository)
        val factory = MainFragmentFactory(viewModelFactory)
        scenario = launchFragmentInContainer(
            themeResId = R.style.Theme_ToDoApp,
            fragmentArgs = bundle,
            factory = factory
        )
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun clickAddTaskButton_navigateToEditTaskFragment() {
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.fab)).perform(click())

        verify(navController).navigate(
            TasksListFragmentDirections.actionTasksListFragmentToEditTaskFragment(
                true
            )
        )
    }

    @Test
    fun clickCheckBox_TaskIsDone() {

        onView(withId(R.id.rvTasksList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<TasksListAdapter.TasksViewHolder>(
                0,
                MyViewActions.clickChildViewWithId(R.id.cbItemTask)
            )
        )

        Assert.assertTrue(repository.tasks[0].isDone)
    }

    @Test
    fun swipeRightTaskItem_deleteItem() {
        onView(withId(R.id.rvTasksList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<TasksListAdapter.TasksViewHolder>(
                0,
                swipeRight()
            )
        )

        Assert.assertTrue(repository.tasks[0].isDone)
    }

    @Test
    fun swipeLeftTaskItem_deleteItem() {
        val startSize = repository.tasks.size

        onView(withId(R.id.rvTasksList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<TasksListAdapter.TasksViewHolder>(
                0,
                swipeLeft()
            )
        )

        assertEquals(startSize - 1, repository.tasks.size)
    }


}