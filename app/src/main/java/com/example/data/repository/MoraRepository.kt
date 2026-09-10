package com.example.data.repository

import com.example.data.db.BudgetDao
import com.example.data.db.CategoryDao
import com.example.data.db.ExpenseDao
import com.example.data.db.SettingDao
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CategoryPresets
import com.example.data.model.ExpenseEntity
import com.example.data.model.SettingEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class MoraRepository(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val settingDao: SettingDao
) {
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    fun getExpensesForMonth(yearMonth: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesForMonth(yearMonth)
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun ensureDefaultCategories(lang: String = "fr") {
        if (categoryDao.getCategoryCount() == 0) {
            categoryDao.insertCategories(CategoryPresets.getDefaultCategories(lang))
        }
    }

    suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    // 6 Months Cutoff Calculation (180 days)
    private fun getSixMonthsAgoCutoff(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -6)
        return cal.timeInMillis
    }

    suspend fun countExpensesOlderThanSixMonths(): Int {
        val cutoff = getSixMonthsAgoCutoff()
        return expenseDao.countExpensesOlderThan(cutoff)
    }

    suspend fun purgeExpensesOlderThanSixMonths(): Int {
        val cutoff = getSixMonthsAgoCutoff()
        return expenseDao.deleteExpensesOlderThan(cutoff)
    }

    suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
    }

    // Budget Operations
    fun getBudgetForMonth(yearMonth: String): Flow<BudgetEntity?> {
        return budgetDao.getBudgetForMonth(yearMonth)
    }

    suspend fun setBudget(yearMonth: String, amount: Double) {
        budgetDao.setBudget(BudgetEntity(yearMonth = yearMonth, overallBudget = amount))
    }

    fun getCategoryBudgetsForMonth(yearMonth: String): Flow<List<CategoryBudgetEntity>> {
        return budgetDao.getCategoryBudgetsForMonth(yearMonth)
    }

    suspend fun setCategoryBudget(yearMonth: String, categoryId: Long, amount: Double) {
        budgetDao.setCategoryBudget(
            CategoryBudgetEntity(
                yearMonth = yearMonth,
                categoryId = categoryId,
                amount = amount
            )
        )
    }

    suspend fun deleteCategoryBudget(yearMonth: String, categoryId: Long) {
        budgetDao.deleteCategoryBudget(yearMonth, categoryId)
    }

    // Settings Operations
    fun getSetting(key: String): Flow<String?> {
        return settingDao.getSetting(key)
    }

    suspend fun getSettingDirect(key: String): String? {
        return settingDao.getSettingDirect(key)
    }

    suspend fun setSetting(key: String, value: String) {
        settingDao.setSetting(SettingEntity(key = key, value = value))
    }

    // Seed Sample Data for interactive demo
    suspend fun seedSampleData(yearMonth: String, categories: List<CategoryEntity>) {
        if (categories.isEmpty()) return

        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        // Set monthly overall budget: 800,000 Ar
        setBudget(yearMonth, 800000.0)

        // Set some category budgets
        val foodCat = categories.find { it.icon == "🍔" || it.name.contains("Food", true) || it.name.contains("Sakafo", true) || it.name.contains("Restauration", true) } ?: categories[0]
        val groceryCat = categories.find { it.icon == "🛒" || it.name.contains("Course", true) || it.name.contains("Tsena", true) } ?: categories.getOrNull(1) ?: categories[0]
        val transportCat = categories.find { it.icon == "🚗" || it.name.contains("Transport", true) } ?: categories.getOrNull(2) ?: categories[0]
        val billsCat = categories.find { it.icon == "⚡" || it.name.contains("Jiro", true) || it.name.contains("Facture", true) } ?: categories.getOrNull(4) ?: categories[0]

        setCategoryBudget(yearMonth, foodCat.id, 200000.0)
        setCategoryBudget(yearMonth, groceryCat.id, 250000.0)
        setCategoryBudget(yearMonth, transportCat.id, 80000.0)
        setCategoryBudget(yearMonth, billsCat.id, 120000.0)

        // Sample expenses: Food is over-budget to demonstrate over-budget alert!
        val sampleExpenses = listOf(
            ExpenseEntity(amount = 145000.0, categoryId = foodCat.id, dateMillis = now - 86400000L * 1, yearMonth = yearMonth, notes = "Resto & sakafo antoandro"),
            ExpenseEntity(amount = 85000.0, categoryId = foodCat.id, dateMillis = now - 86400000L * 3, yearMonth = yearMonth, notes = "Dîner en ville"), // total food = 230,000 > 200,000 (Over budget alert!)
            ExpenseEntity(amount = 180000.0, categoryId = groceryCat.id, dateMillis = now - 86400000L * 2, yearMonth = yearMonth, notes = "Tsena lehibe sabotsy"),
            ExpenseEntity(amount = 45000.0, categoryId = transportCat.id, dateMillis = now - 86400000L * 4, yearMonth = yearMonth, notes = "Lasantsy / Esansy fiara"),
            ExpenseEntity(amount = 110000.0, categoryId = billsCat.id, dateMillis = now - 86400000L * 5, yearMonth = yearMonth, notes = "Jirama sy Internet")
        )

        expenseDao.insertExpenses(sampleExpenses)
    }
}
