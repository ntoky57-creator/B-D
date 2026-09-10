package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.localization.MoraStrings
import com.example.ui.components.BudgetProgressBar
import com.example.ui.components.DonutChart
import com.example.ui.components.OverBudgetAlertCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MoraViewModel

@Composable
fun DashboardScreen(
    viewModel: MoraViewModel,
    modifier: Modifier = Modifier
) {
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()
    val strings by viewModel.strings.collectAsStateWithLifecycle()
    val totalSpent by viewModel.totalSpent.collectAsStateWithLifecycle()
    val monthlyBudget by viewModel.monthlyBudget.collectAsStateWithLifecycle()
    val categoryBudgets by viewModel.categoryBudgets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val donutSlices by viewModel.donutSlices.collectAsStateWithLifecycle()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsStateWithLifecycle()

    val isOverBudget = monthlyBudget > 0 && totalSpent > monthlyBudget
    val exceededOverall = if (isOverBudget) totalSpent - monthlyBudget else 0.0

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 80.dp)
        ) {
            // Month Navigation Bar
            MonthNavigationHeader(
                yearMonth = selectedYearMonth,
                strings = strings,
                onPrev = { viewModel.prevMonth() },
                onNext = { viewModel.nextMonth() },
                onCurrent = { viewModel.goToCurrentMonth() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Over budget alert banner if exceeded
            if (isOverBudget) {
                OverBudgetAlertCard(
                    exceededAmount = exceededOverall,
                    strings = strings,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Monthly Spending Overview Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monthly_overview_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.monthlyOverview.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = MoraStrings.formatAriary(totalSpent),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Overall Budget Progress
                    if (monthlyBudget > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${strings.overallBudget}: ${MoraStrings.formatAriary(monthlyBudget)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val percent = ((totalSpent / monthlyBudget) * 100.0).toInt()
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isOverBudget -> AlertRed
                                    percent >= 80 -> WarningAmber
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        BudgetProgressBar(spent = totalSpent, budget = monthlyBudget, height = 12)

                        Spacer(modifier = Modifier.height(6.dp))
                        val remaining = monthlyBudget - totalSpent
                        if (remaining >= 0) {
                            Text(
                                text = "${strings.remaining}: ${MoraStrings.formatAriary(remaining)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "${strings.overBudgetExceeded}: +${MoraStrings.formatAriary(-remaining)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                        }
                    } else {
                        // Prompt to set monthly budget
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.noBudget,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Donut Chart Breakdown Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = strings.categoryBreakdown,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (monthlyExpenses.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 42.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = strings.noExpensesThisMonth,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strings.addFirstExpense,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.loadSampleData() },
                                modifier = Modifier.testTag("load_sample_data_btn")
                            ) {
                                Text(strings.loadDemoDataAction)
                            }
                        }
                    } else {
                        DonutChart(
                            slices = donutSlices,
                            totalAmount = totalSpent,
                            strings = strings
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Per-Category Spending Breakdown
            if (donutSlices.isNotEmpty()) {
                Text(
                    text = strings.perCategorySpending,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                donutSlices.forEach { slice ->
                    val catBudget = categoryBudgets[slice.categoryId] ?: 0.0
                    val isCatOverBudget = catBudget > 0 && slice.amount > catBudget
                    val exceededAmount = if (isCatOverBudget) slice.amount - catBudget else 0.0

                    CategorySpendingCard(
                        slice = slice,
                        catBudget = catBudget,
                        isOverBudget = isCatOverBudget,
                        exceededAmount = exceededAmount,
                        strings = strings
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Floating Action Button to Add Expense
        FloatingActionButton(
            onClick = { viewModel.openAddExpenseSheet() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_expense")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = strings.addExpenseTitle,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun CategorySpendingCard(
    slice: com.example.ui.components.DonutSlice,
    catBudget: Double,
    isOverBudget: Boolean,
    exceededAmount: Double,
    strings: MoraStrings
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverBudget) {
                AlertRed.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            }
        ),
        border = if (isOverBudget) {
            androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f))
        } else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = slice.color.copy(alpha = 0.2f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = slice.icon, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = slice.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (catBudget > 0) {
                        Text(
                            text = "${strings.spentOf} ${MoraStrings.formatAriary(catBudget)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = MoraStrings.formatAriary(slice.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverBudget) AlertRed else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${"%.1f".format(slice.percentage)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (catBudget > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                BudgetProgressBar(spent = slice.amount, budget = catBudget, height = 6)

                if (isOverBudget) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = AlertRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${strings.overBudgetBadge}: +${MoraStrings.formatAriary(exceededAmount)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthNavigationHeader(
    yearMonth: String,
    strings: MoraStrings,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onCurrent: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            IconButton(
                onClick = onPrev,
                modifier = Modifier.testTag("prev_month_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month"
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onCurrent() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.formatMonthYear(yearMonth),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.testTag("next_month_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month"
                )
            }
        }
    }
}
