package com.zelyder.todoapp.data.network.dto

import com.zelyder.todoapp.domain.enums.Importance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    @SerialName("id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("importance")
    val importance: String,
    @SerialName("done")
    val done: Boolean,
    @SerialName("deadline")
    val deadline: Long?,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
)