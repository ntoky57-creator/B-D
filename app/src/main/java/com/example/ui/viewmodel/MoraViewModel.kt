package com.example.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CategoryEntity
import com.example.data.model.ExpenseEntity
import com.example.data.repository.MoraRepository
import com.example.localization.AppLanguage
import com.example.localization.MoraStrings
import com.example.ui.components.DonutSlice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MoraViewModel(
    private val repository: MoraRepository
) : ViewModel() {

    private val _currentDate = Calendar.getInstance()
    private val yearMonthFormat = SimpleDateFormat("yyyy-MM", Locale.US)

    private val _selectedYearMonth = MutableStateFlow(yearMonthFormat.format(_currentDate.time))
    val selectedYearMonth: StateFlow<String> = _selectedYearMonth.asStateFlow()

    // Language & Strings
    private val _language = MutableStateFlow(AppLanguage.FRENCH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    val strings: StateFlow<MoraStrings> = _language.combine(_selectedYearMonth) { lang, _ ->
        MoraStrings(lang)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MoraStrings(AppLanguage.FRENCH))

    // Theme Mode: "SYSTEM", "LIGHT", "DARK"
    private val _themeMode = MutableStateFlow("SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Categories
    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Expenses
    val monthlyExpenses: StateFlow<List<ExpenseEntity>> = _selectedYearMonth.flatMapLatest { ym ->
        repository.getExpensesForMonth(ym)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall Monthly Budget
    val monthlyBudget: StateFlow<Double> = _selectedYearMonth.flatMapLatest { ym ->
        repository.getBudgetForMonth(ym)
    }.combine(_selectedYearMonth) { budgetEntity, _ ->
        budgetEntity?.overallBudget ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Category Budgets for Month
    val categoryBudgets: StateFlow<Map<Long, Double>> = _selectedYearMonth.flatMapLatest { ym ->
        repository.getCategoryBudgetsForMonth(ym)
    }.combine(_selectedYearMonth) { list, _ ->
        list.associate { it.categoryId to it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Total Spent for current month
    val totalSpent: StateFlow<Double> = monthlyExpenses.combine(_selectedYearMonth) { expenses, _ ->
        expenses.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Donut chart slices derived from expenses and categories
    val donutSlices: StateFlow<List<DonutSlice>> = combine(
        monthlyExpenses,
        categories,
        totalSpent
    ) { expenses, cats, total ->
        if (expenses.isEmpty() || total <= 0.0) {
            emptyList()
        } else {
            val catMap = cats.associateBy { it.id }
            val spendingByCat = expenses.groupBy { it.categoryId }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .filter { it.value > 0.0 }

            spendingByCat.map { (catId, amount) ->
                val cat = catMap[catId]
                val color = try {
                    Color(android.graphics.Color.parseColor(cat?.colorHex ?: "#10B981"))
                } catch (e: Exception) {
                    Color(0xFF10B981)
                }
                DonutSlice(
                    categoryId = catId,
                    name = cat?.name ?: "Autre",
                    icon = cat?.icon ?: "💳",
                    amount = amount,
                    color = color,
                    percentage = ((amount / total) * 100.0).toFloat()
                )
            }.sortedByDescending { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expense History Filtering
    private val _historyFilterCategoryId = MutableStateFlow<Long?>(null)
    val historyFilterCategoryId: StateFlow<Long?> = _historyFilterCategoryId.asStateFlow()

    // Modals / BottomSheets State
    private val _isExpenseSheetOpen = MutableStateFlow(false)
    val isExpenseSheetOpen: StateFlow<Boolean> = _isExpenseSheetOpen.asStateFlow()

    private val _expenseToEdit = MutableStateFlow<ExpenseEntity?>(null)
    val expenseToEdit: StateFlow<ExpenseEntity?> = _expenseToEdit.asStateFlow()

    private val _isCategoryDialogOpen = MutableStateFlow(false)
    val isCategoryDialogOpen: StateFlow<Boolean> = _isCategoryDialogOpen.asStateFlow()

    private val _categoryToEdit = MutableStateFlow<CategoryEntity?>(null)
    val categoryToEdit: StateFlow<CategoryEntity?> = _categoryToEdit.asStateFlow()

    // Notification banner / snackbar message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Load settings and ensure default categories
        viewModelScope.launch {
            repository.ensureDefaultCategories("fr")

            // Load theme
            repository.getSettingDirect("theme_mode")?.let { savedTheme ->
                _themeMode.value = savedTheme
            }

            // Load language
            repository.getSettingDirect("app_language")?.let { savedLangCode ->
                _language.value = AppLanguage.fromCode(savedLangCode)
            }

            // Automatic 6-month data purge check on startup
            checkAndRunAutoPurge()
        }
    }

    private suspend fun checkAndRunAutoPurge() {
        val lastPurge = repository.getSettingDirect("last_auto_purge")?.toLongOrNull() ?: 0L
        val now = System.currentTimeMillis()
        // If more than 7 days since last auto-purge check, run auto purge
        if (now - lastPurge > 7L * 24 * 60 * 60 * 1000L) {
            repository.purgeExpensesOlderThanSixMonths()
            repository.setSetting("last_auto_purge", now.toString())
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun setHistoryFilterCategoryId(catId: Long?) {
        _historyFilterCategoryId.value = catId
    }

    fun openAddExpenseSheet() {
        _expenseToEdit.value = null
        _isExpenseSheetOpen.value = true
    }

    fun openEditExpenseSheet(expense: ExpenseEntity) {
        _expenseToEdit.value = expense
        _isExpenseSheetOpen.value = true
    }

    fun closeExpenseSheet() {
        _isExpenseSheetOpen.value = false
        _expenseToEdit.value = null
    }

    fun openAddCategoryDialog() {
        _categoryToEdit.value = null
        _isCategoryDialogOpen.value = true
    }

    fun openEditCategoryDialog(category: CategoryEntity) {
        _categoryToEdit.value = category
        _isCategoryDialogOpen.value = true
    }

    fun closeCategoryDialog() {
        _isCategoryDialogOpen.value = false
        _categoryToEdit.value = null
    }

    // Month Navigation
    fun nextMonth() {
        adjustMonth(1)
    }

    fun prevMonth() {
        adjustMonth(-1)
    }

    fun goToCurrentMonth() {
        val currentYM = yearMonthFormat.format(Calendar.getInstance().time)
        _selectedYearMonth.value = currentYM
    }

    private fun adjustMonth(delta: Int) {
        try {
            val parts = _selectedYearMonth.value.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Calendar months are 0-based
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, delta)
            }
            _selectedYearMonth.value = yearMonthFormat.format(cal.time)
        } catch (e: Exception) {
            // fallback
            _selectedYearMonth.value = yearMonthFormat.format(Calendar.getInstance().time)
        }
    }

    // Save Expense (Add or Edit)
    fun saveExpense(amount: Double, categoryId: Long, dateMillis: Long, notes: String) {
        viewModelScope.launch {
            val ym = yearMonthFormat.format(Date(dateMillis))
            val existing = _expenseToEdit.value
            if (existing != null) {
                repository.updateExpense(
                    existing.copy(
                        amount = amount,
                        categoryId = categoryId,
                        dateMillis = dateMillis,
                        yearMonth = ym,
                        notes = notes
                    )
                )
            } else {
                repository.insertExpense(
                    ExpenseEntity(
                        amount = amount,
                        categoryId = categoryId,
                        dateMillis = dateMillis,
                        yearMonth = ym,
                        notes = notes
                    )
                )
            }
            closeExpenseSheet()
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Budget Management
    fun setOverallMonthlyBudget(amount: Double) {
        viewModelScope.launch {
            repository.setBudget(_selectedYearMonth.value, amount)
        }
    }

    fun setCategoryBudget(categoryId: Long, amount: Double) {
        viewModelScope.launch {
            if (amount <= 0.0) {
                repository.deleteCategoryBudget(_selectedYearMonth.value, categoryId)
            } else {
                repository.setCategoryBudget(_selectedYearMonth.value, categoryId, amount)
            }
        }
    }

    fun removeCategoryBudget(categoryId: Long) {
        viewModelScope.launch {
            repository.deleteCategoryBudget(_selectedYearMonth.value, categoryId)
        }
    }

    // Category Management (CRUD)
    fun saveCategory(name: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            val existing = _categoryToEdit.value
            if (existing != null) {
                repository.updateCategory(
                    existing.copy(
                        name = name,
                        icon = icon,
                        colorHex = colorHex
                    )
                )
            } else {
                val currentCount = categories.value.size
                repository.insertCategory(
                    CategoryEntity(
                        name = name,
                        icon = icon,
                        colorHex = colorHex,
                        sortOrder = currentCount
                    )
                )
            }
            closeCategoryDialog()
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // Settings
    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        viewModelScope.launch {
            repository.setSetting("theme_mode", mode)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        viewModelScope.launch {
            repository.setSetting("app_language", lang.code)
        }
    }

    // Purge Data Older Than 6 Months
    fun purgeHistoryOlderThanSixMonths() {
        viewModelScope.launch {
            val deletedCount = repository.purgeExpensesOlderThanSixMonths()
            val msg = if (deletedCount > 0) {
                "$deletedCount ${strings.value.purgeCompleted}"
            } else {
                strings.value.noOldDataToPurge
            }
            _userMessage.value = msg
        }
    }

    // Delete All Data Manually
    fun deleteAllHistory() {
        viewModelScope.launch {
            repository.deleteAllExpenses()
            _userMessage.value = strings.value.deleteConfirmMsg
        }
    }

    // Seed Sample Data for Demo
    fun loadSampleData() {
        viewModelScope.launch {
            repository.seedSampleData(_selectedYearMonth.value, categories.value)
            _userMessage.value = "Demo data loaded!"
        }
    }
}

class MoraViewModelFactory(
    private val repository: MoraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoraViewModel::class.java)) {
            return MoraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
