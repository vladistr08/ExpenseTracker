package com.example.expensetracker.repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetracker.model.ExpenseModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ExpenseRepository {
    private const val EXPENSE_COLLECTION = "Expenses"

    private fun getFirestoreInstance() = Firebase.firestore

    suspend fun addExpense(expense: ExpenseModel): Result<Unit> = withContext(Dispatchers.IO) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        expense.date = current

        return@withContext try {
            getFirestoreInstance().collection(EXPENSE_COLLECTION).add(expense).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getExpenses(userId: String): LiveData<Result<List<ExpenseModel>>> {
        val expensesLiveData = MutableLiveData<Result<List<ExpenseModel>>>()

        getFirestoreInstance().collection(EXPENSE_COLLECTION)
            .whereEqualTo("userId", userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                expensesLiveData.value = Result.failure(e)
                return@addSnapshotListener
            }

            val expenses = snapshot?.map { doc -> doc.toObject(ExpenseModel::class.java) }
            expensesLiveData.value = Result.success(expenses.orEmpty())
        }

        return expensesLiveData
    }

    suspend fun updateExpense(updatedExpense: ExpenseModel): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Find the document with the matching 'id'
            val querySnapshot = getFirestoreInstance().collection(EXPENSE_COLLECTION)
                .whereEqualTo("id", updatedExpense.id)
                .get()
                .await()

            // Assuming 'id' is unique and there's only one matching document
            val documentSnapshot = querySnapshot.documents.firstOrNull()
            if (documentSnapshot != null) {
                getFirestoreInstance().collection(EXPENSE_COLLECTION)
                    .document(documentSnapshot.id)
                    .set(updatedExpense)
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Expense not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(expenseId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val querySnapshot = getFirestoreInstance().collection(EXPENSE_COLLECTION)
                .whereEqualTo("id", expenseId)
                .get()
                .await()

            // Assuming there's only one expense with this ID
            if (!querySnapshot.isEmpty) {
                for (document in querySnapshot.documents) {
                    document.reference.delete().await()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Expense not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}