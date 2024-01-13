package com.example.expensetracker.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ExpenseModel(
    val id: String = "no_id",
    val userId: String = "no_user_id",
    val title: String = "no_title",
    val amount: Double = 0.0,
    val description: String = "no_description",
    val currency: Currency = Currency.getDefault(),
    var date: String = "NO_VALID_DATE"
)