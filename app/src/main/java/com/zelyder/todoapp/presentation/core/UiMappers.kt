package com.zelyder.todoapp.presentation.core

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

fun formatDate(day: Int, month: Int, year: Int): String {
    val fullMonth = when(month) {
        1 -> "января"
        2 -> "февраля"
        3 -> "матра"
        4 -> "апреля"
        5 -> "мая"
        6 -> "июня"
        7 -> "июля"
        8 -> "августа"
        9 -> "сентября"
        10 -> "октября"
        11 -> "ноября"
        12 -> "декабря"
        else -> throw IllegalArgumentException()
    }
    return "$day $fullMonth $year"
}

fun isOverdue(date: String): Boolean {
    val calendar = Calendar.getInstance()
    val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
    val nowMonth = calendar.get(Calendar.MONTH) +1
    val nowYear = calendar.get(Calendar.YEAR)

    val arr = date.split(" ")
    val day = arr[0].toInt()
    val month = fromMonthToInt(arr[1])
    val year = arr[2].toInt()

    val nowInMillis: Long = Calendar.getInstance().run {
        set(nowYear, nowMonth, nowDay)
        timeInMillis
    }
    val dateInMillis: Long = Calendar.getInstance().run {
        set(year, month, day)
        timeInMillis
    }

    return dateInMillis < nowInMillis
}

fun fromMonthToInt(month: String): Int = when(month) {
    "января" -> 1
    "февраля" -> 2
    "матра" -> 3
    "апреля" -> 4
    "мая" -> 5
    "июня" -> 6
    "июля" -> 7
    "августа" -> 8
    "сентября" -> 9
    "октября" -> 10
    "ноября" -> 11
    "декабря" -> 12
    else -> throw IllegalArgumentException()
}