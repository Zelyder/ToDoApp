package com.zelyder.todoapp.presentation.tasks_list

import junit.framework.TestCase
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.zelyder.todoapp.MainCoroutineRule
import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.domain.repositories.TasksListRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
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

    @MockK
    lateinit var mockRepository: TasksListRepository


    @Before
    public override fun setUp() {
        MockKAnnotations.init(this)
        viewModel = TasksListViewModel(mockRepository, coroutineRule.testDispatcher)
        super.setUp()
    }

    @After
    public override fun tearDown() {

    }

    @Test
    fun `list should be shown without completed tasks`() {
        coEvery { mockRepository.getTasks() } returns tasks
        coEvery { mockRepository.getCountOfDone() } returns tasks.count { it.isDone }
        every { mockTasksObserver.onChanged(any()) } answers {}
        viewModel.tasks.observeForever(mockTasksObserver)

        viewModel.updateList()
        verify { mockTasksObserver.onChanged(tasks.filter { !it.isDone }) }
    }

    @Test
    fun `All tasks should be shown`() {
        coEvery { mockRepository.getTasks() } returns tasks
        coEvery { mockRepository.getCountOfDone() } returns tasks.count { it.isDone }
        every { mockTasksObserver.onChanged(any()) } answers {}
        viewModel.tasks.observeForever(mockTasksObserver)
        viewModel.toggleVisibility()
        verify { mockTasksObserver.onChanged(tasks) }
    }

    @Test
    fun `should show the number of completed tasks`() {
        coEvery { mockRepository.getTasks() } returns tasks
        coEvery { mockRepository.getCountOfDone() } returns tasks.count { it.isDone }
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
        val localTasks = tasks.toMutableList()
        val taskSlot = slot<Task>()

        coEvery { mockRepository.getTasks() } returns localTasks
        coEvery { mockTasksObserver.onChanged(any()) } answers {}
        coEvery { mockRepository.getCountOfDone() } returns tasks.count { it.isDone }


        coEvery { mockRepository.editTask(capture(taskSlot)) } coAnswers {
            val findTask = localTasks.find { it.id == taskSlot.captured.id }
            findTask?.text = newTask.text
            findTask?.isDone = newTask.isDone
            findTask?.importance = newTask.importance
            findTask?.date = newTask.date
        }
        viewModel.tasks.observeForever(mockTasksObserver)
        viewModel.editTask(newTask)
        assertEquals(localTasks.find { it.id == task.id }, newTask)
    }

    @Test
    fun `the task must be deleted`() {
        val task = Task("f856521e-0208-4d7c-84fd-ec22bc39c886", "test 1", false, Importance.NONE)
        val localTasks = tasks.toMutableList()
        coEvery { mockRepository.getTasks() } returns localTasks
        coEvery { mockTasksObserver.onChanged(any()) } answers {}
        coEvery { mockRepository.getCountOfDone() } returns tasks.count { it.isDone }

        val idSlot = slot<String>()
        coEvery { mockRepository.deleteTaskById(capture(idSlot)) } coAnswers {
            localTasks.removeIf { it.id == idSlot.captured }
        }

        viewModel.tasks.observeForever(mockTasksObserver)

        viewModel.toggleVisibility()
        viewModel.deleteTask(task)
        val resList = tasks.filter { it.id != task.id }
        assertEquals(localTasks, resList)
        verify { mockTasksObserver.onChanged(resList) }

    }

    @Test
    fun `the task must be added`() {
        val task =
            Task("d6ca9df2-ce3d-4553-a69c-bc7c53bf714d", "new task", importance = Importance.HIGH)
        val localTasks = tasks.toMutableList()
        coEvery { mockRepository.getTasks() } returns localTasks
        coEvery { mockRepository.getCountOfDone() } returns tasks.count { it.isDone }
        coEvery { mockTasksObserver.onChanged(any()) } answers {}

        val taskSlot = slot<Task>()
        coEvery { mockRepository.addTask(capture(taskSlot)) } coAnswers {
            localTasks.add(taskSlot.captured)
        }

        viewModel.tasks.observeForever(mockTasksObserver)

        viewModel.toggleVisibility()
        viewModel.addTask(task)
        val resList = tasks.toMutableList()
        resList.add(task)
        assertEquals(resList, localTasks)
        verify { mockTasksObserver.onChanged(resList) }

    }

    companion object {
        lateinit var viewModel: TasksListViewModel


        val tasks = listOf(
            Task("f856521e-0208-4d7c-84fd-ec22bc39c886", "test 1", false, Importance.NONE),
            Task("8b9a796c-e6e7-4543-93d7-4fda251cef30", "test 2", false, Importance.HIGH),
            Task("eebc4ce8-4741-420f-b302-a721afe6db25", "test 3", true, Importance.LOW),
            Task("27987619-7c11-461a-8cb0-7b3c87d14b47", "test 4", true, Importance.NONE),
        )
    }

}

