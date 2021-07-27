package com.zelyder.todoapp.presentation.core

import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import java.util.*

class UiMappersKtTest : TestCase() {

    private val locale: Locale = Locale("ru", "RU")

    @Test
    fun `cast long to date`() {
        assertEquals("01 января 1970", 0L.toDate(locale = locale))
        assertEquals("08 июля 2021", 1625724865939L.toDate(locale = locale))
        assertEquals("08 июля 2021\n 09:14", 1625724865939L.toDate(true, locale = locale))
        assertNotSame("08 июля 2021\n 09:14", 0L.toDate(locale = locale))
        assertEquals("21 июля 2021", 1626814800000L.toDate(locale = locale))
        assertEquals("21 июля 2021\n 00:00", 1626814800000L.toDate(true, locale = locale))
    }

    @Test
    fun `cast date to long`() {

    }

    @Test
    fun `test is today`() {
        assertEquals(true, isToday(Calendar.getInstance().timeInMillis.toDate(locale = locale)))
        assertEquals(false, isToday("28 июня 2021"))
        assertEquals(false, isToday("27 июня 2020"))
    }
}

