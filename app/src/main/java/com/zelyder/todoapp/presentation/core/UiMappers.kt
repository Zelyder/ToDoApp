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

fun isToday(date: String, locale: Locale = Locale.getDefault()): Boolean {
    val nowTime = Calendar.getInstance().timeInMillis.toDate(locale = locale)
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

fun String.toDateInMillis(withTime: Boolean = false, locale: Locale = Locale.getDefault()): Long {
    val simpleDateFormat =  if(withTime) {
        SimpleDateFormat("dd MMMM yyyy\n HH:mm", locale)
    }else {
        SimpleDateFormat("dd MMMM yyyy", locale)
    }
    return simpleDateFormat.parse(this)?.time ?: 0L
}

fun Long.toDate(showTime: Boolean = false, locale: Locale = Locale.getDefault()): String {
    return if(showTime) {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy\n HH:mm", locale)
        simpleDateFormat.format(Date(this))

    }else {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", locale)
        simpleDateFormat.format(Date(this))
    }
}

