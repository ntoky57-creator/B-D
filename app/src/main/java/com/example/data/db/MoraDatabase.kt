package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CategoryPresets
import com.example.data.model.ExpenseEntity
import com.example.data.model.SettingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoryEntity::class,
        ExpenseEntity::class,
        BudgetEntity::class,
        CategoryBudgetEntity::class,
        SettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoraDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: MoraDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())): MoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoraDatabase::class.java,
                    "mora_offline.db"
                )
                .addCallback(MoraDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class MoraDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: MoraDatabase) {
            val categoryDao = db.categoryDao()
            if (categoryDao.getCategoryCount() == 0) {
                val defaults = CategoryPresets.getDefaultCategories("fr")
                categoryDao.insertCategories(defaults)
            }
        }
    }
}
