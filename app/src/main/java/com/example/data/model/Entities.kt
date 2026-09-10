package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // Emoji
    val colorHex: String,
    val sortOrder: Int = 0
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val dateMillis: Long,
    val yearMonth: String, // format "YYYY-MM" e.g. "2026-09"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "monthly_budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val yearMonth: String, // format "YYYY-MM"
    val overallBudget: Double
)

@Entity(tableName = "category_budgets")
data class CategoryBudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val yearMonth: String, // format "YYYY-MM"
    val categoryId: Long,
    val amount: Double
)

@Entity(tableName = "app_settings")
data class SettingEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
