package com.zelyder.todoapp.presentation.tasks_list

import junit.framework.TestCase
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.zelyder.todoapp.MainCoroutineRule
import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.domain.repositories.FakeTasksListRepository
import com.zelyder.todoapp.presentation.core.toDateInMillis
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class TasksListViewModelTest : TestCase() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @MockK
    lateinit var mockTasksObserver: Observer<List<Task>>

    @MockK
    lateinit var mockDoneCount: Observer<Int>

    @MockK
    lateinit var mockIsHidden: Observer<Boolean>

    lateinit var fakeRepository: FakeTasksListRepository


    @Before
    public override fun setUp() {
        MockKAnnotations.init(this)
        fakeRepository = FakeTasksListRepository(tasks, today)
        viewModel = TasksListViewModel(fakeRepository, coroutineRule.testDispatcher)
        super.setUp()
    }

    @After
    public override fun tearDown() {

    }

    @Test
    fun `list should be shown without completed tasks`() {
        every { mockTasksObserver.onChanged(any()) } answers {}
        viewModel.tasks.observeForever(mockTasksObserver)

        viewModel.updateList()
        verify { mockTasksObserver.onChanged(tasks.filter { !it.isDone }) }
    }

    @Test
    fun `All tasks should be shown`() {
        every { mockTasksObserver.onChanged(any()) } answers {}
        viewModel.tasks.observeForever(mockTasksObserver)
        viewModel.toggleVisibility()
        verify { mockTasksObserver.onChanged(tasks) }
    }

    @Test
    fun `should show the number of completed tasks`() {
        every { mockDoneCount.onChanged(any()) } answers {}
        viewModel.doneCount.observeForever(mockDoneCount)
        viewModel.updateList()
        verify { mockDoneCount.onChanged(tasks.count { it.isDone }) }
    }

    @Test
    fun `the task must be edited`() {
        val task = Task("f856521e-0208-4d7c-84fd-ec22bc39c886", "test 1", false, Importance.NONE)
        val newTask =
            Task("f856521e-0208-4d7c-84fd-ec22bc39c886", "new test", false, Importance.HIGH)
        coEvery { mockTasksObserver.onChanged(any()) } answers {}

        viewModel.tasks.observeForever(mockTasksObserver)
        viewModel.editTask(newTask)
        assertEquals(fakeRepository.tasks.find { it.id == task.id }, newTask)
    }

    @Test
    fun `the task must be deleted`() {
        val task = Task("f856521e-0208-4d7c-84fd-ec22bc39c886", "test 1", false, Importance.NONE)
        coEvery { mockTasksObserver.onChanged(any()) } answers {}

        viewModel.tasks.observeForever(mockTasksObserver)

        viewModel.toggleVisibility()
        viewModel.deleteTask(task)
        val resList = tasks.filter { it.id != task.id }
        assertEquals(fakeRepository.tasks, resList)
        verify { mockTasksObserver.onChanged(resList) }

    }

    @Test
    fun `the task must be added`() {
        val task =
            Task("d6ca9df2-ce3d-4553-a69c-bc7c53bf714d", "new task", importance = Importance.HIGH)
        coEvery { mockTasksObserver.onChanged(any()) } answers {}

        viewModel.tasks.observeForever(mockTasksObserver)

        viewModel.toggleVisibility()
        viewModel.addTask(task)
        val resList = tasks.toMutableList()
        resList.add(task)
        assertEquals(resList, fakeRepository.tasks)
        verify { mockTasksObserver.onChanged(resList) }

    }

    companion object {
        lateinit var viewModel: TasksListViewModel
        val today = "24 июля 2021"

        val tasks = listOf(
            Task("f856521e-0208-4d7c-84fd-ec22bc39c886", "test 1", false, Importance.NONE),
            Task("8b9a796c-e6e7-4543-93d7-4fda251cef30", "test 2", false, Importance.HIGH),
            Task("eebc4ce8-4741-420f-b302-a721afe6db25", "test 3", true, Importance.LOW),
            Task("27987619-7c11-461a-8cb0-7b3c87d14b47", "test 4", true, Importance.NONE),
        )
    }

}

