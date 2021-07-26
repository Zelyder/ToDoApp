package com.zelyder.todoapp.presentation.tasks_list

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.zelyder.todoapp.R
import com.zelyder.todoapp.domain.repositories.FakeTasksListRepository
import com.zelyder.todoapp.presentation.core.MainFragmentFactory
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
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


}