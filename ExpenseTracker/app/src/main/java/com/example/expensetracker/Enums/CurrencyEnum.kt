package com.example.expensetracker.Enums

import java.util.EnumMap

enum class Currency(val symbol: String) {
    USD("USD"),
    EUR("EUR"),
    GBP("GBP"),
    JPY("JPY"),
    RON("RON");

    companion object {
        private val exchangeRates: EnumMap<Currency, Double> = EnumMap(Currency::class.java)

        fun getDefault() = RON
        fun convertToUSD(amount: Double, currency: Currency): Double {
            exchangeRates[USD] = 1.0
            exchangeRates[EUR] = 1.1
            exchangeRates[GBP] = 1.27
            exchangeRates[JPY] = 0.0069
            exchangeRates[RON] = 0.22

            return amount * exchangeRates[currency]!!
        }

        fun convertFromUSD(amount: Double, currency: Currency): Double {
            exchangeRates[USD] = 1.0
            exchangeRates[EUR] = 0.91
            exchangeRates[GBP] = 0.79
            exchangeRates[JPY] = 145.21
            exchangeRates[RON] = 4.53

            return amount * exchangeRates[currency]!!
        }

        fun convertCurrencies(amount: Double, currencyFrom: Currency, currencyTo: Currency): Double{
            return convertFromUSD(convertToUSD(amount, currencyFrom), currencyTo)
        }
    }
}