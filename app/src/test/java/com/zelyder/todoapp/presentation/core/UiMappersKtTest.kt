package com.zelyder.todoapp.presentation.core

import junit.framework.TestCase
import org.junit.Test

class UiMappersKtTest : TestCase() {
    @Test
    fun testIsToday() {
        assertEquals(true, isToday("27 июня 2021"))
        assertEquals(false, isToday("28 июня 2021"))
        assertEquals(false, isToday("27 июня 2020"))

    }
    @Test
    fun testGetTimeDiffInMillis() {
        val time = calculateTimeDiffInMillis(16, 35)
        assertTrue(time > 1000)
    }
}