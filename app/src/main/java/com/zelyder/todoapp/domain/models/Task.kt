package com.zelyder.todoapp.domain.models

import android.os.Parcelable
import com.zelyder.todoapp.domain.enums.Importance
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Task(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    var isDone: Boolean = false,
    var importance: Importance = Importance.NONE,
    var date: String? = null
): Parcelable


