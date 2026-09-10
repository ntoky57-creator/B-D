package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.MoraDatabase
import com.example.data.repository.MoraRepository
import com.example.ui.components.CategoryEditDialog
import com.example.ui.components.ExpenseFormSheet
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MoraViewModel
import com.example.ui.viewmodel.MoraViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current.applicationContext
            val database = remember { MoraDatabase.getDatabase(context) }
            val repository = remember {
                MoraRepository(
                    categoryDao = database.categoryDao(),
                    expenseDao = database.expenseDao(),
                    budgetDao = database.budgetDao(),
                    settingDao = database.settingDao()
                )
            }
            val viewModel: MoraViewModel = viewModel(
                factory = MoraViewModelFactory(repository)
            )

            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val useDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = useDarkTheme) {
                MoraApp(viewModel = viewModel)
            }
        }
    }
}

enum class MoraTab(
    val icon: ImageVector,
    val testTag: String
) {
    DASHBOARD(Icons.Default.PieChart, "tab_dashboard"),
    HISTORY(Icons.Default.ReceiptLong, "tab_history"),
    BUDGET(Icons.Default.AccountBalanceWallet, "tab_budget"),
    SETTINGS(Icons.Default.Settings, "tab_settings")
}

@Composable
fun MoraApp(viewModel: MoraViewModel) {
    val strings by viewModel.strings.collectAsStateWithLifecycle()
    val isExpenseSheetOpen by viewModel.isExpenseSheetOpen.collectAsStateWithLifecycle()
    val expenseToEdit by viewModel.expenseToEdit.collectAsStateWithLifecycle()
    val isCategoryDialogOpen by viewModel.isCategoryDialogOpen.collectAsStateWithLifecycle()
    val categoryToEdit by viewModel.categoryToEdit.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mora_main_scaffold"),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("mora_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = androidx.compose.ui.unit.Dp(6f)
            ) {
                MoraTab.entries.forEachIndexed { index, tab ->
                    val label = when (tab) {
                        MoraTab.DASHBOARD -> strings.tabDashboard
                        MoraTab.HISTORY -> strings.tabHistory
                        MoraTab.BUDGET -> strings.tabBudget
                        MoraTab.SETTINGS -> strings.tabSettings
                    }
                    val isSelected = selectedTabIndex == index

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = selectedTabIndex,
                label = "tab_crossfade"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> DashboardScreen(viewModel = viewModel)
                    1 -> HistoryScreen(viewModel = viewModel)
                    2 -> BudgetScreen(viewModel = viewModel)
                    3 -> SettingsScreen(viewModel = viewModel)
                }
            }
        }

        // Add / Edit Expense Sheet
        if (isExpenseSheetOpen) {
            ExpenseFormSheet(
                expenseToEdit = expenseToEdit,
                categories = categories,
                strings = strings,
                onDismiss = { viewModel.closeExpenseSheet() },
                onSave = { amount, categoryId, dateMillis, notes ->
                    viewModel.saveExpense(amount, categoryId, dateMillis, notes)
                }
            )
        }

        // Add / Edit Category Dialog
        if (isCategoryDialogOpen) {
            CategoryEditDialog(
                categoryToEdit = categoryToEdit,
                strings = strings,
                onDismiss = { viewModel.closeCategoryDialog() },
                onSave = { name, icon, colorHex ->
                    viewModel.saveCategory(name, icon, colorHex)
                }
            )
        }
    }
}

// Kept for screenshot and unit tests compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
