package com.zelyder.todoapp.domain.repositories

import com.zelyder.todoapp.MainCoroutineRule
import com.zelyder.todoapp.data.mappers.toDto
import com.zelyder.todoapp.data.mappers.toEntity
import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto
import com.zelyder.todoapp.data.storage.entities.DeletedTaskEntity
import com.zelyder.todoapp.data.storage.entities.TaskEntity
import com.zelyder.todoapp.domain.datasources.DeletedTasksDataSource
import com.zelyder.todoapp.domain.datasources.TasksLocalDataSource
import com.zelyder.todoapp.domain.datasources.TasksYandexDataSource
import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.enums.NetworkResult
import com.zelyder.todoapp.domain.enums.NetworkStatus
import com.zelyder.todoapp.domain.models.Task
import com.zelyder.todoapp.presentation.core.NetworkStatusTrackerImpl
import com.zelyder.todoapp.presentation.core.isToday
import com.zelyder.todoapp.presentation.core.toDateInMillis
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class TasksListRepositoryImplTest : TestCase() {

    lateinit var repository: TasksListRepositoryImpl

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @MockK
    lateinit var mockTasksLocalDataSource: TasksLocalDataSource

    @MockK
    lateinit var mockDeletedTasksDataSource: DeletedTasksDataSource

    @MockK
    lateinit var mockYandexDataSource: TasksYandexDataSource

    @MockK
    lateinit var mockNetworkTracker: NetworkStatusTrackerImpl

    @Before
    public override fun setUp() {
        MockKAnnotations.init(this)
        repository = TasksListRepositoryImpl(
            mockTasksLocalDataSource,
            mockDeletedTasksDataSource,
            mockYandexDataSource,
            mockNetworkTracker,
            coroutineRule.testDispatcher
        )
        super.setUp()
    }

    @After
    public override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `Should show the number of tasks for today`() {
        coEvery { mockTasksLocalDataSource.getTasks() } returns tasks

        runBlockingTest {
            val countTodayTasks = repository.getCountTodayTasks()
            assertEquals(tasks.count { it.deadline == today }, countTodayTasks)
        }

    }

    @Test
    fun `should sync local data with cloud`() {
        coEvery { mockNetworkTracker.getNetworkStatus() } returns flowOf(NetworkStatus.Available)
        var localTasks = tasks
        val cloudTasks = modifiedTasks.map { it.toDto() }.toMutableList()
        var deletedList = listOf(DeletedTaskEntity("f856521e-0208-4d7c-84fd-ec22bc39c886"))

        val tasksSlot = slot<List<TaskEntity>>()
        val addAndDeleteSlot = slot<AddAndDeleteDto>()
        val addList = mutableListOf<TaskDto>()

        coEvery { mockTasksLocalDataSource.getTasks() } returns localTasks
        coEvery { mockTasksLocalDataSource.deleteAllTasks() } coAnswers { localTasks = emptyList() }
        coEvery { mockTasksLocalDataSource.saveTasks(capture(tasksSlot)) } coAnswers { localTasks = tasksSlot.captured }

        coEvery { mockYandexDataSource.getTasks() } returns NetworkResult.Success(cloudTasks)
        coEvery { mockYandexDataSource.updateTasks(capture(addAndDeleteSlot)) } coAnswers {
            cloudTasks.removeAll { it.id in addAndDeleteSlot.captured.deletedIdList }
            cloudTasks.onEachIndexed { cloudIndex, cloudTaskDto ->
                addAndDeleteSlot.captured.addList.onEach { addListTaskDto ->
                    if (cloudTaskDto.id == addListTaskDto.id) {
                        cloudTasks[cloudIndex] = addListTaskDto
                    } else {
                        if (addListTaskDto !in addList && addListTaskDto.id !in cloudTasks.map { it.id }) {
                            addList.add(addListTaskDto)
                        }
                    }
                }
            }
            cloudTasks.addAll(addList)
            NetworkResult.Success(cloudTasks)
        }

        coEvery { mockDeletedTasksDataSource.getAll() } returns deletedList
        coEvery { mockDeletedTasksDataSource.clear() } answers {deletedList = emptyList()}

        runBlockingTest {
            repository.checkInternetAndSync()
            assertEquals(mergedTasks, localTasks)
            assertEquals(mergedTasks, cloudTasks.map { it.toEntity()})
        }

    }

    companion object {
        val today = "24 июля 2021".toDateInMillis()
        val tasks = listOf(
            TaskEntity(
                "8b9a796c-e6e7-4543-93d7-4fda251cef30",
                "test 2",
                Importance.HIGH,
                false,
                "25 июля 2021".toDateInMillis(),
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "22 июля 2021".toDateInMillis()
            ),
            TaskEntity(
                "eebc4ce8-4741-420f-b302-a721afe6db25",
                "test 3",
                Importance.LOW,
                true,
                today,
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "22 июля 2021".toDateInMillis()
            ),
            TaskEntity(
                "27987619-7c11-461a-8cb0-7b3c87d14b47",
                "test 4",
                Importance.NONE,
                true,
                "23 июля 2021".toDateInMillis(),
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "22 июля 2021".toDateInMillis()
            ),
            TaskEntity(
                "c8782fde-cc8f-41a3-9e2a-2ed68c404f90",
                "test 5",
                Importance.HIGH,
                false,
                "28 июля 2021".toDateInMillis(),
                createdAt = 0L,
                updatedAt = "24 июля 2021".toDateInMillis()
            ),
        )

        // Differences from the previous list: removed "task 3", not deleted yet "test 1", changed "test 2",
        // not added yet "test 5"
        val modifiedTasks = listOf(
            TaskEntity(
                "f856521e-0208-4d7c-84fd-ec22bc39c886",
                "test 1",
                Importance.NONE,
                false,
                today,
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "22 июля 2021".toDateInMillis()
            ),
            TaskEntity(
                "8b9a796c-e6e7-4543-93d7-4fda251cef30",
                "new test 2",
                Importance.HIGH,
                false,
                "27 июля 2021".toDateInMillis(),
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "24 июля 2021".toDateInMillis()
            ),
            TaskEntity(
                "27987619-7c11-461a-8cb0-7b3c87d14b47",
                "test 4",
                Importance.NONE,
                true,
                "23 июля 2021".toDateInMillis(),
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "22 июля 2021".toDateInMillis()
            ),
        )
        val mergedTasks = listOf(
            TaskEntity(
                "8b9a796c-e6e7-4543-93d7-4fda251cef30",
                "new test 2",
                Importance.HIGH,
                false,
                "27 июля 2021".toDateInMillis(),
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "24 июля 2021".toDateInMillis()
            ),
            TaskEntity(
                "27987619-7c11-461a-8cb0-7b3c87d14b47",
                "test 4",
                Importance.NONE,
                true,
                "23 июля 2021".toDateInMillis(),
                createdAt = "22 июля 2021".toDateInMillis(),
                updatedAt = "22 июля 2021".toDateInMillis()
            ),
            TaskEntity(
                "c8782fde-cc8f-41a3-9e2a-2ed68c404f90",
                "test 5",
                Importance.HIGH,
                false,
                "28 июля 2021".toDateInMillis(),
                createdAt = "24 июля 2021".toDateInMillis(),
                updatedAt = "24 июля 2021".toDateInMillis()
            ),
        )
    }
}