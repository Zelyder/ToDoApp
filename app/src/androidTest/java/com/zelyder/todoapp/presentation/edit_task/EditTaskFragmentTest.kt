package com.zelyder.todoapp.presentation.edit_task

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zelyder.todoapp.R
import com.zelyder.todoapp.domain.enums.EditScreenExitStatus
import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.domain.repositories.FakeTasksListRepository
import com.zelyder.todoapp.presentation.core.MainFragmentFactory
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import com.zelyder.todoapp.presentation.tasks_list.TasksListFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
@RunWith(AndroidJUnit4::class)
class EditTaskFragmentTest {

    private lateinit var scenario: FragmentScenario<EditTaskFragment>
    private lateinit var repository: FakeTasksListRepository

    @Before
    fun setup() {
        val bundle = Bundle()
        bundle.putBoolean("isNewTask", true)
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
    fun addNewTask_exitWithStatusAdd() {
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        val taskText = "new task"
        onView(withId(R.id.edit_task_et_text)).perform(replaceText(taskText))
        onView(withId(R.id.edit_task_tv_save)).perform(click())

        scenario.onFragment {

        }

        Mockito.verify(navController).navigate(
            EditTaskFragmentDirections.actionEditTaskFragmentToTasksListFragment(
                EditScreenExitStatus.ADD, any())
        )
    }
}