package com.example.expensetracker.model

enum class ExpenseSortOptionsEnum(val displayName: String) {
    DATE_OLDEST("Date (Oldest)"),
    DATE_NEWEST("Date (Newest)"),
    AMOUNT_HIGHEST("Amount (Highest)"),
    AMOUNT_LOWEST("Amount (Lowest)");

    companion object {
        fun getDefault() = ExpenseSortOptionsEnum.DATE_OLDEST
    }
}