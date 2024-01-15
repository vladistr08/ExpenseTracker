import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.Enums.Currency
import com.example.expensetracker.model.ExpenseModel
import com.example.expensetracker.repository.ExpenseRepository
import kotlinx.coroutines.launch

interface ExpenseAdapterCallback {
    fun onExpenseDeletionError(message: String)
    fun onExpenseUpdateError(message: String)
}

class ExpensesAdapter(private val lifecycleScope: LifecycleCoroutineScope, private val callback: ExpenseAdapterCallback)  : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {

    interface OnExpenseUpdateListener {
        fun onExpenseUpdated()
    }

    var updateListener: OnExpenseUpdateListener? = null

    private var expenses = listOf<ExpenseModel>()

    fun submitList(newList: List<ExpenseModel>) {
        expenses = newList
        notifyDataSetChanged()
    }

    fun GetExpenses(): List<ExpenseModel>{
        return expenses
    }

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: EditText = view.findViewById(R.id.titleTextView)
        val amountTextView: EditText = view.findViewById(R.id.amountTextView)
        val descriptionTextView: EditText = view.findViewById(R.id.descriptionTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val updateButton: Button = view.findViewById(R.id.updateButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(itemView)
    }

    private fun deleteExpense(expenseId: String, position: Int) {
        lifecycleScope.launch {
            val result = ExpenseRepository.deleteExpense(expenseId)
            if (result.isSuccess) {
                expenses = expenses.filterNot { it.id == expenseId }
                notifyItemRemoved(position)
            } else {
                result.exceptionOrNull()?.let {
                    callback.onExpenseDeletionError(it.message ?: "Error deleting expense.")
                }
            }
        }
    }

    private fun updateExpense(updatedExpense: ExpenseModel, position: Int) {
        lifecycleScope.launch {
            val result = ExpenseRepository.updateExpense(updatedExpense)
            if (result.isSuccess) {
                notifyItemChanged(position)
                updateListener?.onExpenseUpdated()
            } else {
                result.exceptionOrNull()?.let {
                    callback.onExpenseUpdateError(it.message ?: "Error updating expense.")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.titleTextView.setText(expense.title)
        holder.amountTextView.setText(expense.amount.toString() + ' ' + expense.currency.symbol)
        holder.descriptionTextView.setText(expense.description)
        holder.dateTextView.text = "Date: " + expense.date

        holder.deleteButton.setOnClickListener {
            deleteExpense(expense.id, position)
        }

        holder.updateButton.setOnClickListener {
            val amountCurrencyText = holder.amountTextView.text.toString()
            val (amountText, currencySymbol) = amountCurrencyText.split(" ", limit = 2)
            var amount = amountText.toDoubleOrNull() ?: 0.0
            val currency = Currency.values().find { it.symbol == currencySymbol } ?: Currency.getDefault()

            if(expense.currency != currency && expense.amount == amount){
                amount = Currency.convertCurrencies(amount, expense.currency, currency)
            }

            val updatedExpense = expense.copy(
                id = expense.id,
                userId = expense.userId,
                title = holder.titleTextView.text.toString(),
                amount = amount,
                currency = currency,
                description = holder.descriptionTextView.text.toString(),
                date = expense.date,
            )
            updateExpense(updatedExpense, position)
        }
    }

    override fun getItemCount() = expenses.size

    @SuppressLint("NotifyDataSetChanged")
    fun setExpenses(expenses: List<ExpenseModel>) {
        this.expenses = expenses
        notifyDataSetChanged()
    }
}