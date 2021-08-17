package com.zelyder.todoapp.presentation.edit_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zelyder.todoapp.di.components.AppScope
import com.zelyder.todoapp.domain.enums.Importance
import javax.inject.Inject

@AppScope
class EditTaskViewModel @Inject constructor(): ViewModel() {

    private val _importance = MutableLiveData<Importance>()
    private val _deadline = MutableLiveData<String?>()

   val importance: LiveData<Importance> get() = _importance
   val deadline: LiveData<String?> get() = _deadline

    fun setImportance(importance: Importance) {
        _importance.value = importance
    }

    fun setDeadline(date: String?) {
        _deadline.value = date
    }
}