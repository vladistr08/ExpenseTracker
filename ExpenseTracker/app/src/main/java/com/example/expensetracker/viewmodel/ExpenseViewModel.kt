package com.example.expensetracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.model.ExpenseModel
import com.example.expensetracker.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ExpenseViewModel : BaseViewModel() {
    private val _expenses = MutableLiveData<List<ExpenseModel>>()
    val expenses: LiveData<List<ExpenseModel>> = _expenses

    fun loadExpenses() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid.toString()

        viewModelScope.launch {
            ExpenseRepository.getExpenses(userId).observeForever { result ->
                result.onSuccess { expensesList ->
                    _expenses.value = expensesList
                }
                result.onFailure { exception ->
                    handleError(exception)
                }
            }
        }
    }
}