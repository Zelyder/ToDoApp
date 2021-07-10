package com.zelyder.todoapp.presentation.core


import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.*


class Dialogs : DatePickerDialog.OnDateSetListener {

    private var day = 0
    private var month = 0
    private var year = 0


    var savedDay = 0
        private set
    var savedMonth = 0
        private set
    var savedYear = 0
        private set

    private lateinit var _doAfterDateSet: () -> Unit

    fun openDatePicker(context: Context, doAfterDateSet: () -> Unit) {
        getDateCalendar()
        _doAfterDateSet = doAfterDateSet
        val datePicker = DatePickerDialog(context, this, year, month, day)
        datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePicker.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        _doAfterDateSet()

    }

    private fun getDateCalendar() {
        val calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
    }
}