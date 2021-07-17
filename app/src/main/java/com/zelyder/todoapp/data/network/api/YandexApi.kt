package com.zelyder.todoapp.data.network.api

import com.zelyder.todoapp.data.network.dto.AddAndDeleteDto
import com.zelyder.todoapp.data.network.dto.TaskDto
import retrofit2.Call
import retrofit2.http.*

interface YandexApi {

    @POST("tasks")
    suspend fun sendTask(@Body task: TaskDto): TaskDto

    @GET("tasks")
    suspend fun getTasks(): List<TaskDto>

    @PUT("tasks/{id}")
    suspend fun updateTask(@Body task: TaskDto, @Path("id") id: String): TaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): TaskDto

    @PUT("tasks")
    suspend fun updateTasks(@Body addAndDeleteDto: AddAndDeleteDto): List<TaskDto>

}