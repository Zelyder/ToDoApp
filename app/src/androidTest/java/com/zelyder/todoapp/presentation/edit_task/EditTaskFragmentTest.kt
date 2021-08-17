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

    private lateinit var repository: FakeTasksListRepository
    private lateinit var navController: NavController
    private lateinit var factory: MainFragmentFactory

    @Before
    fun setup() {
        repository = FakeTasksListRepository()
        val viewModelFactory = ViewModelFactory(repository)
        factory = MainFragmentFactory(viewModelFactory)
        navController = Mockito.mock(NavController::class.java)
    }

    @Test
    fun addNewTask_exitWithStatusAdd() {
        val bundle = Bundle()
        bundle.putBoolean("isNewTask", true)
        val scenario: FragmentScenario<EditTaskFragment> = launchFragmentInContainer(
            themeResId = R.style.Theme_ToDoApp,
            fragmentArgs = bundle,
            factory = factory
        )
        scenario.moveToState(Lifecycle.State.STARTED)

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        val taskText = "new task"
        onView(withId(R.id.edit_task_et_text)).perform(replaceText(taskText))
        onView(withId(R.id.edit_task_tv_save)).perform(click())

        Mockito.verify(navController).navigate(
            EditTaskFragmentDirections.actionEditTaskFragmentToTasksListFragment(
                EditScreenExitStatus.ADD, any())
        )
    }

    @Test
    fun clickClose_navigateUp() {
        val bundle = Bundle()
        bundle.putBoolean("isNewTask", true)
        val scenario: FragmentScenario<EditTaskFragment> = launchFragmentInContainer(
            themeResId = R.style.Theme_ToDoApp,
            fragmentArgs = bundle,
            factory = factory
        )
        scenario.moveToState(Lifecycle.State.STARTED)

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.edit_task_img_close)).perform(click())
        Mockito.verify(navController).navigateUp()
    }

    @Test
    fun clickDelete_exitWithStatusDelete() {
        val task = Task("0", "waste task")
        val bundle = Bundle()
        bundle.putBoolean("isNewTask", false)
        bundle.putParcelable("task", task)

        val scenario: FragmentScenario<EditTaskFragment> =  launchFragmentInContainer(
            themeResId = R.style.Theme_ToDoApp,
            fragmentArgs = bundle,
            factory = factory
        )
        scenario.moveToState(Lifecycle.State.STARTED)

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.edit_task_tv_delete)).perform(click())

        Mockito.verify(navController).navigate(
            EditTaskFragmentDirections.actionEditTaskFragmentToTasksListFragment(
                EditScreenExitStatus.DELETE, task)
        )
    }

    @Test
    fun clickSave_exitWithStatusEdit() {
        val task = Task("0", "some task")
        val bundle = Bundle()
        bundle.putBoolean("isNewTask", false)
        bundle.putParcelable("task", task)

        val scenario: FragmentScenario<EditTaskFragment> =  launchFragmentInContainer(
            themeResId = R.style.Theme_ToDoApp,
            fragmentArgs = bundle,
            factory = factory
        )
        scenario.moveToState(Lifecycle.State.STARTED)

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        val newTaskText = "edited task"
        onView(withId(R.id.edit_task_et_text)).perform(replaceText(newTaskText))
        onView(withId(R.id.edit_task_tv_save)).perform(click())
        task.text = newTaskText

        Mockito.verify(navController).navigate(
            EditTaskFragmentDirections.actionEditTaskFragmentToTasksListFragment(
                EditScreenExitStatus.EDIT, task)
        )
    }
}