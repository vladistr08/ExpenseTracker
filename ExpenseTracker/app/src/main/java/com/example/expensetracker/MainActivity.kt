package com.example.expensetracker

import ExpenseAdapterCallback
import ExpensesAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.model.ExpenseSortOptionsEnum
import com.example.expensetracker.utils.Utils.Companion.sortExpensesByOption
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity(), ExpenseAdapterCallback {

    private lateinit var expensesRecyclerView: RecyclerView
    private lateinit var addExpenseButton: Button
    private lateinit var analyticsButton: Button
    private lateinit var logoutButton: Button

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expensesAdapter: ExpensesAdapter

    override fun onExpenseDeletionError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onExpenseUpdateError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        setupObservers()
        viewModel.loadExpenses()

        initializeUI()
    }

    private fun initializeUI() {
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView)
        addExpenseButton = findViewById(R.id.addExpenseButton)
        analyticsButton = findViewById(R.id.analyticsButton)
        logoutButton = findViewById(R.id.logoutButton)

        expensesAdapter = ExpensesAdapter(lifecycleScope, this)
        expensesRecyclerView.adapter = expensesAdapter

        expensesRecyclerView = findViewById(R.id.expensesRecyclerView)
        expensesRecyclerView.layoutManager = LinearLayoutManager(this)

        addExpenseButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        analyticsButton.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            logoutUser()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val sortingSpinner = findViewById<Spinner>(R.id.sortingSpinner)
        val sortingOptions = ExpenseSortOptionsEnum.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortingOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortingSpinner.adapter = adapter

        sortingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = ExpenseSortOptionsEnum.values()[position]
                val sortedExpenses = sortExpensesByOption(expensesAdapter.GetExpenses(), selectedOption)
                expensesAdapter.submitList(sortedExpenses)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected (optional)
            }
        }
    }

    private fun setupObservers() {
        viewModel.expenses.observe(this, Observer { expensesList ->
            expensesAdapter.setExpenses(expensesList)
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()

        // Clear the saved timestamp
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("lastLoginTimestamp")
            apply()
        }
    }
}
