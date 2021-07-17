package com.zelyder.todoapp.presentation.core

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

fun isOverdue(date: String): Boolean {
    val dates = getDatesInMillis(date)
    return dates.first < dates.second
}

fun isToday(date: String): Boolean {
    val nowTime = Calendar.getInstance().timeInMillis.toDate()
    return date == nowTime
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
    val nowInMillis: Long = Calendar.getInstance().timeInMillis.toDate().toDateInMillis()
    val dateInMillis: Long = date.toDateInMillis()
    return dateInMillis to nowInMillis
}

fun String.toDateInMillis(withTime: Boolean = false): Long {
    val simpleDateFormat =  if(withTime) {
        SimpleDateFormat("dd MMMM yyyy\n HH:mm", Locale.getDefault())
    }else {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    }
    return simpleDateFormat.parse(this)?.time ?: 0L
}

fun Long.toDate(showTime: Boolean = false): String {
    return if(showTime) {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy\n HH:mm", Locale.getDefault())
        simpleDateFormat.format(Date(this))

    }else {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        simpleDateFormat.format(Date(this))
    }
}

