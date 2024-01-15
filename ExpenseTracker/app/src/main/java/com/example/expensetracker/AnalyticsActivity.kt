package com.example.expensetracker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.Enums.Currency
import com.example.expensetracker.Enums.ExpenseSortOptionsEnum
import com.example.expensetracker.model.ExpenseModel
import com.example.expensetracker.repository.ExpenseRepository
import com.example.expensetracker.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Console
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToLong

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var tvTotalSpending: TextView
    private lateinit var currencySpinner: Spinner
    private lateinit var backButton: Button
    private var lastSelectedPosition = 0

    @SuppressLint("SetTextI18n")
    private fun changeAmountText(userId: String, selectedCurrency: Currency){
        lifecycleScope.launch {
            tvTotalSpending.text = BigDecimal(ExpenseRepository.getTotalSpentAmount(userId, selectedCurrency)).setScale(2, RoundingMode.HALF_UP).toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        tvTotalSpending = findViewById(R.id.tvTotalSpending)
        currencySpinner = findViewById(R.id.currencySpinner)
        setupCurrencySpinner()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid.toString()


        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lastSelectedPosition = position
                changeAmountText(userId, Currency.values()[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                changeAmountText(userId, Currency.values()[lastSelectedPosition])
            }
        }

    }

    private fun setupCurrencySpinner() {
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Currency.values().map { it.symbol })
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = currencyAdapter
        currencySpinner.setSelection(currencyAdapter.getPosition(Currency.getDefault().symbol))
    }
}
