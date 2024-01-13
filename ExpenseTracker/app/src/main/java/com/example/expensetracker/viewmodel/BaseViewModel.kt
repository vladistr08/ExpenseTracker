package com.example.expensetracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    protected val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    protected fun handleError(exception: Throwable) {
        _error.postValue(exception.message ?: "An unknown error occurred")
    }
}
