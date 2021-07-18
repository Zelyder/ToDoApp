package com.zelyder.todoapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddAndDeleteDto(
    @SerialName("deleted")
    val deletedIdList: List<String>,
    @SerialName("other")
    val addList: List<TaskDto>
)
