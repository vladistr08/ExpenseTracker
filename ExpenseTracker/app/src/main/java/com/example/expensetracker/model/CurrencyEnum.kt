package com.example.expensetracker.model

enum class Currency(val symbol: String) {
    USD("USD"),
    EUR("EUR"),
    GBP("GBP"),
    JPY("JPY"),
    RON("RON");

    companion object {
        fun getDefault() = RON
    }
}