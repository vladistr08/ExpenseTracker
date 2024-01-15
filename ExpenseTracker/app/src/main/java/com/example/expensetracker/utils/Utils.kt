package com.example.expensetracker.utils

import com.example.expensetracker.Enums.Currency
import com.example.expensetracker.model.ExpenseModel
import com.example.expensetracker.Enums.ExpenseSortOptionsEnum

class Utils {
    companion object{
        fun sortExpensesByOption(expenses: List<ExpenseModel>, option: ExpenseSortOptionsEnum): List<ExpenseModel> {
            return when (option) {
                ExpenseSortOptionsEnum.DATE_OLDEST -> expenses.sortedBy { it.date }
                ExpenseSortOptionsEnum.DATE_NEWEST -> expenses.sortedByDescending { it.date }
                ExpenseSortOptionsEnum.AMOUNT_HIGHEST -> expenses.sortedByDescending { Currency.convertToUSD(it.amount, it.currency) }
                ExpenseSortOptionsEnum.AMOUNT_LOWEST -> expenses.sortedBy { Currency.convertToUSD(it.amount, it.currency) }
            }
        }
    }
}