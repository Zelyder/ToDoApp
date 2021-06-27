package com.zelyder.todoapp.domain.models

import android.os.Parcelable
import com.zelyder.todoapp.domain.enums.Importance
import kotlinx.android.parcel.Parcelize

var countTasks:Long = 0

@Parcelize
data class Task(
    val id: Long = countTasks + 1,
    var text: String,
    var isDone: Boolean = false,
    var importance: Importance = Importance.NONE,
    var dateTime: String? = null
): Parcelable {
    init {
        countTasks++
    }
}


