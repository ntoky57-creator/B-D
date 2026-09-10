package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.localization.MoraStrings
import com.example.ui.components.BudgetProgressBar
import com.example.ui.theme.AlertRed
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MoraViewModel

@Composable
fun BudgetScreen(
    viewModel: MoraViewModel,
    modifier: Modifier = Modifier
) {
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()
    val strings by viewModel.strings.collectAsStateWithLifecycle()
    val monthlyBudget by viewModel.monthlyBudget.collectAsStateWithLifecycle()
    val totalSpent by viewModel.totalSpent.collectAsStateWithLifecycle()
    val categoryBudgets by viewModel.categoryBudgets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsStateWithLifecycle()

    var showOverallBudgetDialog by remember { mutableStateOf(false) }
    var categoryForBudgetDialog by remember { mutableStateOf<CategoryEntity?>(null) }

    val spendingByCat = remember(monthlyExpenses) {
        monthlyExpenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    val isOverallOverBudget = monthlyBudget > 0 && totalSpent > monthlyBudget

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        // Month Header
        MonthNavigationHeader(
            yearMonth = selectedYearMonth,
            strings = strings,
            onPrev = { viewModel.prevMonth() },
            onNext = { viewModel.nextMonth() },
            onCurrent = { viewModel.goToCurrentMonth() }
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("budget_manager_list"),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Overall Budget Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("overall_budget_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = strings.overallBudget,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = strings.currentBudget,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showOverallBudgetDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("edit_overall_budget_btn")
                            ) {
                                Text(
                                    text = if (monthlyBudget > 0) strings.editEntry else strings.setBudgetAction,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (monthlyBudget > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = strings.totalSpent,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = MoraStrings.formatAriary(totalSpent),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOverallOverBudget) AlertRed else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = strings.overallBudget,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = MoraStrings.formatAriary(monthlyBudget),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            BudgetProgressBar(spent = totalSpent, budget = monthlyBudget, height = 12)

                            Spacer(modifier = Modifier.height(6.dp))
                            val remaining = monthlyBudget - totalSpent
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val percent = ((totalSpent / monthlyBudget) * 100.0).toInt()
                                Text(
                                    text = "$percent% ${strings.spentOf} ${MoraStrings.formatAriary(monthlyBudget)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (remaining >= 0) {
                                    Text(
                                        text = "${strings.remaining}: ${MoraStrings.formatAriary(remaining)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        text = "${strings.overBudgetBadge}: +${MoraStrings.formatAriary(-remaining)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = strings.setOverallBudgetPrompt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Category Budgets Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.categoryBudgetsTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Category Budgets List
            items(categories, key = { it.id }) { category ->
                val budget = categoryBudgets[category.id] ?: 0.0
                val spent = spendingByCat[category.id] ?: 0.0
                val isCatOverBudget = budget > 0 && spent > budget
                val exceeded = if (isCatOverBudget) spent - budget else 0.0

                val catColor = try {
                    Color(android.graphics.Color.parseColor(category.colorHex))
                } catch (e: Exception) {
                    MaterialTheme.colorScheme.primary
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCatOverBudget) AlertRed.copy(alpha = 0.06f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isCatOverBudget) androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f))
                    else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_item_${category.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = catColor.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = category.icon, fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (budget > 0) "${strings.currentBudget}: ${MoraStrings.formatAriary(budget)}"
                                    else strings.noBudget,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { categoryForBudgetDialog = category },
                                modifier = Modifier.testTag("edit_cat_budget_${category.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Budget",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (budget > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            BudgetProgressBar(spent = spent, budget = budget, height = 8)

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val percent = ((spent / budget) * 100.0).toInt()
                                Text(
                                    text = "$percent% (${MoraStrings.formatAriary(spent)})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCatOverBudget) AlertRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (isCatOverBudget) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = AlertRed,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "+${MoraStrings.formatAriary(exceeded)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AlertRed
                                        )
                                    }
                                } else {
                                    val rem = budget - spent
                                    Text(
                                        text = "${strings.remaining}: ${MoraStrings.formatAriary(rem)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        } else if (spent > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${strings.totalSpent}: ${MoraStrings.formatAriary(spent)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Overall Monthly Budget Dialog
    if (showOverallBudgetDialog) {
        var budgetInput by remember {
            mutableStateOf(
                if (monthlyBudget > 0) {
                    if (monthlyBudget % 1.0 == 0.0) monthlyBudget.toLong().toString()
                    else monthlyBudget.toString()
                } else ""
            )
        }

        AlertDialog(
            onDismissRequest = { showOverallBudgetDialog = false },
            title = {
                Text(
                    text = strings.setOverallBudgetPrompt,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = strings.amountLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                budgetInput = input
                            }
                        },
                        placeholder = { Text("Ex: 800000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("overall_budget_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = budgetInput.toDoubleOrNull() ?: 0.0
                        viewModel.setOverallMonthlyBudget(amount)
                        showOverallBudgetDialog = false
                    },
                    modifier = Modifier.testTag("save_overall_budget_btn")
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showOverallBudgetDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Edit Category Budget Dialog
    if (categoryForBudgetDialog != null) {
        val cat = categoryForBudgetDialog!!
        val currentCatBudget = categoryBudgets[cat.id] ?: 0.0
        var budgetInput by remember {
            mutableStateOf(
                if (currentCatBudget > 0) {
                    if (currentCatBudget % 1.0 == 0.0) currentCatBudget.toLong().toString()
                    else currentCatBudget.toString()
                } else ""
            )
        }

        AlertDialog(
            onDismissRequest = { categoryForBudgetDialog = null },
            title = {
                Text(
                    text = "${cat.icon} ${cat.name}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = strings.setBudgetForCategory,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                budgetInput = input
                            }
                        },
                        placeholder = { Text("Ex: 150000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cat_budget_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = budgetInput.toDoubleOrNull() ?: 0.0
                        viewModel.setCategoryBudget(cat.id, amount)
                        categoryForBudgetDialog = null
                    },
                    modifier = Modifier.testTag("save_cat_budget_btn")
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                Row {
                    if (currentCatBudget > 0) {
                        OutlinedButton(
                            onClick = {
                                viewModel.removeCategoryBudget(cat.id)
                                categoryForBudgetDialog = null
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(strings.removeBudget, color = AlertRed)
                        }
                    }
                    OutlinedButton(onClick = { categoryForBudgetDialog = null }) {
                        Text(strings.cancel)
                    }
                }
            }
        )
    }
}
