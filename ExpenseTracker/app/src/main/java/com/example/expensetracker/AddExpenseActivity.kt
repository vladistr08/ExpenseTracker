package com.example.expensetracker

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.ExpenseModel
import com.example.expensetracker.repository.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AddExpenseActivity: AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveExpenseButton: Button
    private lateinit var backButton: Button
    private lateinit var currencySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        titleEditText = findViewById(R.id.titleEditText)
        amountEditText = findViewById(R.id.amountEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveExpenseButton = findViewById(R.id.saveExpenseButton)
        backButton = findViewById(R.id.backButton)

        currencySpinner = findViewById(R.id.currencySpinner)
        setupCurrencySpinner()

        saveExpenseButton.setOnClickListener {
            saveExpense()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupCurrencySpinner() {
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Currency.values().map { it.symbol })
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = currencyAdapter
        currencySpinner.setSelection(currencyAdapter.getPosition(Currency.getDefault().symbol))
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    private fun saveExpense() {
        lifecycleScope.launch {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val description = descriptionEditText.text.toString()
            val title = titleEditText.text.toString()

            if (amount != null && description.isNotEmpty()) {
                val selectedCurrencySymbol = currencySpinner.selectedItem.toString()
                val selectedCurrency = Currency.values().first { it.symbol == selectedCurrencySymbol }
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid.toString()

                val expense = ExpenseModel(generateUUID(), userId, title, amount, description, selectedCurrency)
                val result = ExpenseRepository.addExpense(expense)

                if (result.isSuccess) {
                    Toast.makeText(this@AddExpenseActivity, "Expense saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else if (result.isFailure) {
                    Toast.makeText(this@AddExpenseActivity, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddExpenseActivity, "Please enter valid data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
