package com.zelyder.todoapp.presentation.core

import junit.framework.TestCase

class UiMappersKtTest : TestCase() {
    fun testIsToday() {
        assertEquals(true, isToday("27 июня 2021"))
        assertEquals(false, isToday("28 июня 2021"))
        assertEquals(false, isToday("27 июня 2020"))

    }
}