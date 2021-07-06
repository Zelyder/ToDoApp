package com.zelyder.todoapp.presentation.core

import java.util.*
import kotlin.math.abs

fun formatDate(day: Int, month: Int, year: Int): String {
    val fullMonth = when (month) {
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
    val dates = getDatesInMillis(date)
    return dates.first < dates.second
}

fun isToday(date: String): Boolean {
    val calendar = Calendar.getInstance()
    val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
    val nowMonth = calendar.get(Calendar.MONTH) + 1
    val nowYear = calendar.get(Calendar.YEAR)

    val arr = date.split(" ")
    val day = arr[0].toInt()
    val month = fromMonthToInt(arr[1])
    val year = arr[2].toInt()

    return nowDay == day && nowMonth == month && nowYear == year
}

fun calculateTimeDiffInMillis(hourOfDay: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
    val nowMonth = calendar.get(Calendar.MONTH)
    val nowYear = calendar.get(Calendar.YEAR)
    val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
    val nowMinute = calendar.get(Calendar.MINUTE)


    val nowInMillis: Long = Calendar.getInstance().run {
        set(nowYear, nowMonth, nowDay, nowHour, nowMinute)
        timeInMillis
    }
    val dateInMillis: Long = Calendar.getInstance().run {
        set(
            nowYear, nowMonth,
            nowDay ,
            hourOfDay, minute
        )
        if (nowHour * 60 + nowMinute >= hourOfDay * 60 + minute) timeInMillis + 24*3600*1000 else timeInMillis
    }
    return abs(dateInMillis - nowInMillis)
}

private fun getDatesInMillis(date: String): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
    val nowMonth = calendar.get(Calendar.MONTH) + 1
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
    return Pair(dateInMillis, nowInMillis)
}

fun fromMonthToInt(month: String): Int = when (month) {
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