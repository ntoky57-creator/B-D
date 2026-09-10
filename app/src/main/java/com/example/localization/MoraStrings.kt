package com.example.localization

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppLanguage(val code: String, val label: String) {
    MALAGASY("mg", "Malagasy"),
    FRENCH("fr", "Français (France)"),
    ENGLISH("en", "English (Américain)");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: FRENCH
        }
    }
}

class MoraStrings(val lang: AppLanguage) {

    val appName: String get() = "MORA"
    val appSlogan: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Ny fitantananao nohamoraina"
        AppLanguage.FRENCH -> "Votre gestion simplifiée"
        AppLanguage.ENGLISH -> "Your finance simplified"
    }

    // Tabs
    val tabDashboard: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Topy Maso"
        AppLanguage.FRENCH -> "Tableau de Bord"
        AppLanguage.ENGLISH -> "Dashboard"
    }
    val tabHistory: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tantara"
        AppLanguage.FRENCH -> "Historique"
        AppLanguage.ENGLISH -> "History"
    }
    val tabBudget: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tetibola"
        AppLanguage.FRENCH -> "Budgets"
        AppLanguage.ENGLISH -> "Budgets"
    }
    val tabSettings: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fikirakirana"
        AppLanguage.FRENCH -> "Paramètres"
        AppLanguage.ENGLISH -> "Settings"
    }

    // Dashboard
    val monthlyOverview: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fandaniana amin'ny volana"
        AppLanguage.FRENCH -> "Aperçu Mensuel"
        AppLanguage.ENGLISH -> "Monthly Spending"
    }
    val totalSpent: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fitambaran'ny Lany"
        AppLanguage.FRENCH -> "Total Dépensé"
        AppLanguage.ENGLISH -> "Total Spent"
    }
    val overallBudget: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tetibola ankapobeny"
        AppLanguage.FRENCH -> "Budget Global"
        AppLanguage.ENGLISH -> "Overall Budget"
    }
    val remaining: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Sisa azo lany"
        AppLanguage.FRENCH -> "Solde Restant"
        AppLanguage.ENGLISH -> "Remaining"
    }
    val overBudgetExceeded: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Nihoatra ny tetibola"
        AppLanguage.FRENCH -> "Dépassement du budget"
        AppLanguage.ENGLISH -> "Over Budget"
    }
    val overBudgetAlertTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "FAMPITANDREMANA : NIHOATRA NY TETIBOLA !"
        AppLanguage.FRENCH -> "ALERTE : BUDGET MENSUEL DÉPASSÉ !"
        AppLanguage.ENGLISH -> "ALERT: OVER BUDGET LIMIT!"
    }
    val overBudgetAlertMsg: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Nihoatra ny fetra napetrakao ho an'ity volana ity ianao."
        AppLanguage.FRENCH -> "Vos dépenses totales dépassent le budget alloué pour ce mois."
        AppLanguage.ENGLISH -> "Your total spending has exceeded the budget set for this month."
    }
    val categoryBreakdown: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fizarana ara-tsokajy"
        AppLanguage.FRENCH -> "Répartition par Catégorie"
        AppLanguage.ENGLISH -> "Category Breakdown"
    }
    val perCategorySpending: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fandaniana isaky ny sokajy"
        AppLanguage.FRENCH -> "Dépenses par Catégorie"
        AppLanguage.ENGLISH -> "Spending by Category"
    }
    val noExpensesThisMonth: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tsy mbola misy fandaniana voasoratra tamin'ity volana ity."
        AppLanguage.FRENCH -> "Aucune dépense enregistrée pour ce mois."
        AppLanguage.ENGLISH -> "No expenses recorded for this month."
    }
    val addFirstExpense: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tsindrio ny (+) hampidirana fandaniana voalohany."
        AppLanguage.FRENCH -> "Appuyez sur (+) pour ajouter votre première dépense."
        AppLanguage.ENGLISH -> "Tap (+) to record your first expense."
    }
    val budgetProgress: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fivoaran'ny tetibola"
        AppLanguage.FRENCH -> "Progression du budget"
        AppLanguage.ENGLISH -> "Budget Progress"
    }

    // Expense History
    val allCategories: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Daholo"
        AppLanguage.FRENCH -> "Toutes"
        AppLanguage.ENGLISH -> "All"
    }
    val runningTotal: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fitambarany"
        AppLanguage.FRENCH -> "Total Cumulé"
        AppLanguage.ENGLISH -> "Running Total"
    }
    val transactionsCount: String get() = when (lang) {
        AppLanguage.MALAGASY -> "hetsika"
        AppLanguage.FRENCH -> "transactions"
        AppLanguage.ENGLISH -> "transactions"
    }
    val searchOrFilter: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Sivana"
        AppLanguage.FRENCH -> "Filtres"
        AppLanguage.ENGLISH -> "Filter"
    }
    val editEntry: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Hanova"
        AppLanguage.FRENCH -> "Modifier"
        AppLanguage.ENGLISH -> "Edit"
    }
    val deleteEntry: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fafao"
        AppLanguage.FRENCH -> "Supprimer"
        AppLanguage.ENGLISH -> "Delete"
    }
    val deleteConfirmTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Hamafa ity fandaniana ity?"
        AppLanguage.FRENCH -> "Supprimer cette transaction ?"
        AppLanguage.ENGLISH -> "Delete this transaction?"
    }
    val deleteConfirmMsg: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tena ho fafanao ve ity fandaniana ity? Tsy azo averina intsony."
        AppLanguage.FRENCH -> "Voulez-vous vraiment supprimer cette dépense ? Cette action est irréversible."
        AppLanguage.ENGLISH -> "Are you sure you want to delete this expense? This cannot be undone."
    }

    // Add / Edit Form
    val addExpenseTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Ampidiro Fandaniana"
        AppLanguage.FRENCH -> "Ajouter une Dépense"
        AppLanguage.ENGLISH -> "Add Expense"
    }
    val editExpenseTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Hanova Fandaniana"
        AppLanguage.FRENCH -> "Modifier la Dépense"
        AppLanguage.ENGLISH -> "Edit Expense"
    }
    val amountLabel: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Vola lany (Ar)"
        AppLanguage.FRENCH -> "Montant (Ar)"
        AppLanguage.ENGLISH -> "Amount (Ar)"
    }
    val selectCategory: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Misafidiana Sokajy"
        AppLanguage.FRENCH -> "Choisir la Catégorie"
        AppLanguage.ENGLISH -> "Select Category"
    }
    val dateLabel: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Daty"
        AppLanguage.FRENCH -> "Date"
        AppLanguage.ENGLISH -> "Date"
    }
    val today: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Androany"
        AppLanguage.FRENCH -> "Aujourd'hui"
        AppLanguage.ENGLISH -> "Today"
    }
    val yesterday: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Omaly"
        AppLanguage.FRENCH -> "Hier"
        AppLanguage.ENGLISH -> "Yesterday"
    }
    val notesLabel: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fanamarihana (tsy voatery)"
        AppLanguage.FRENCH -> "Notes (facultatif)"
        AppLanguage.ENGLISH -> "Notes (optional)"
    }
    val notesPlaceholder: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Ohatra: Sakafo antoandro, lasantsy..."
        AppLanguage.FRENCH -> "Ex: Déjeuner, carburant, courses..."
        AppLanguage.ENGLISH -> "E.g. Lunch, groceries, taxi..."
    }
    val save: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tehirizo"
        AppLanguage.FRENCH -> "Enregistrer"
        AppLanguage.ENGLISH -> "Save"
    }
    val cancel: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Aoka ihany"
        AppLanguage.FRENCH -> "Annuler"
        AppLanguage.ENGLISH -> "Cancel"
    }
    val invalidAmountError: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Azafady ampidiro ny isa marina mihoatra ny 0"
        AppLanguage.FRENCH -> "Veuillez entrer un montant valide supérieur à 0"
        AppLanguage.ENGLISH -> "Please enter a valid amount greater than 0"
    }
    val selectCategoryError: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Azafady misafidiana sokajy iray"
        AppLanguage.FRENCH -> "Veuillez sélectionner une catégorie"
        AppLanguage.ENGLISH -> "Please select a category"
    }

    // Budget Manager
    val budgetManagerTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Mpikarakara Tetibola"
        AppLanguage.FRENCH -> "Gestionnaire de Budget"
        AppLanguage.ENGLISH -> "Budget Manager"
    }
    val setOverallBudgetPrompt: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fetra ankapobeny amin'ny volana"
        AppLanguage.FRENCH -> "Définir le budget mensuel global"
        AppLanguage.ENGLISH -> "Set overall monthly budget"
    }
    val currentBudget: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tetibola amin'izao"
        AppLanguage.FRENCH -> "Budget Actuel"
        AppLanguage.ENGLISH -> "Current Budget"
    }
    val categoryBudgetsTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tetibola isaky ny sokajy"
        AppLanguage.FRENCH -> "Budgets par Catégorie"
        AppLanguage.ENGLISH -> "Category Budgets"
    }
    val setBudgetForCategory: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fetra ho an'ny sokajy"
        AppLanguage.FRENCH -> "Budget pour cette catégorie"
        AppLanguage.ENGLISH -> "Budget for category"
    }
    val noBudget: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tsy misy fetra"
        AppLanguage.FRENCH -> "Aucun budget"
        AppLanguage.ENGLISH -> "No budget set"
    }
    val setBudgetAction: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Mamaritra"
        AppLanguage.FRENCH -> "Définir"
        AppLanguage.ENGLISH -> "Set"
    }
    val removeBudget: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Esory ny fetra"
        AppLanguage.FRENCH -> "Supprimer le budget"
        AppLanguage.ENGLISH -> "Remove Budget"
    }
    val spentOf: String get() = when (lang) {
        AppLanguage.MALAGASY -> "lany tamin'ny"
        AppLanguage.FRENCH -> "dépensé sur"
        AppLanguage.ENGLISH -> "spent of"
    }
    val overBudgetBadge: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Nihoatra"
        AppLanguage.FRENCH -> "Dépassé"
        AppLanguage.ENGLISH -> "Exceeded"
    }

    // Settings
    val settingsTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fikirakirana"
        AppLanguage.FRENCH -> "Paramètres"
        AppLanguage.ENGLISH -> "Settings"
    }
    val appearanceSection: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Endriky ny fampiharana"
        AppLanguage.FRENCH -> "Apparence"
        AppLanguage.ENGLISH -> "Appearance"
    }
    val themeMode: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Endrika (Maizina / Mazava)"
        AppLanguage.FRENCH -> "Thème (Sombre / Clair)"
        AppLanguage.ENGLISH -> "Theme (Dark / Light)"
    }
    val themeSystem: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Rafitra"
        AppLanguage.FRENCH -> "Système"
        AppLanguage.ENGLISH -> "System"
    }
    val themeLight: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Mazava"
        AppLanguage.FRENCH -> "Clair"
        AppLanguage.ENGLISH -> "Light"
    }
    val themeDark: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Maizina"
        AppLanguage.FRENCH -> "Sombre"
        AppLanguage.ENGLISH -> "Dark"
    }
    val languageSection: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Safidy Fiteny"
        AppLanguage.FRENCH -> "Langue de l'Application"
        AppLanguage.ENGLISH -> "App Language"
    }
    val categoryManagementSection: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fitantanana ny Sokajy"
        AppLanguage.FRENCH -> "Gestion des Catégories"
        AppLanguage.ENGLISH -> "Category Management"
    }
    val addCategoryBtn: String get() = when (lang) {
        AppLanguage.MALAGASY -> "+ Hanampy Sokajy Vaovao"
        AppLanguage.FRENCH -> "+ Ajouter une Catégorie"
        AppLanguage.ENGLISH -> "+ Add New Category"
    }
    val editCategoryTitle: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Hanova Sokajy"
        AppLanguage.FRENCH -> "Modifier la Catégorie"
        AppLanguage.ENGLISH -> "Edit Category"
    }
    val categoryNameLabel: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Anaran'ny sokajy"
        AppLanguage.FRENCH -> "Nom de la catégorie"
        AppLanguage.ENGLISH -> "Category Name"
    }
    val chooseEmojiLabel: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Sary famantarana (Emoji 16)"
        AppLanguage.FRENCH -> "Icône Emoji (16 pré-réglages)"
        AppLanguage.ENGLISH -> "Emoji Icon (16 presets)"
    }
    val chooseColorLabel: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Loko (16 karazana)"
        AppLanguage.FRENCH -> "Couleur (16 pré-réglages)"
        AppLanguage.ENGLISH -> "Color (16 presets)"
    }
    val deleteCategoryConfirm: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Hamafa ity sokajy ity?"
        AppLanguage.FRENCH -> "Supprimer cette catégorie ?"
        AppLanguage.ENGLISH -> "Delete this category?"
    }

    // Offline & Purge
    val offlineDataSection: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tahiry 100% Tsy Mila Aterineto"
        AppLanguage.FRENCH -> "Stockage 100% Hors-Ligne"
        AppLanguage.ENGLISH -> "100% Offline Storage"
    }
    val offlineDataDescription: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Ny angona rehetra dia voatahiry soa aman-tsara ao amin'ny SQLite an'ny findainao. Tsy mila kaonty, tsy misy tambajotra, tsy misy serveur."
        AppLanguage.FRENCH -> "Toutes vos dépenses sont stockées localement en SQLite sur votre appareil. Aucun compte, aucun réseau, aucun serveur externe."
        AppLanguage.ENGLISH -> "All data lives securely in local SQLite on-device. No account, no network, no remote server."
    }
    val autoPurgeInfo: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Famafana ho azy isaky ny 6 volana: Voatahiry mandritra ny 6 volana ny tantaran'ny fandaniana."
        AppLanguage.FRENCH -> "Purge automatique tous les 6 mois : L'historique antérieur à 6 mois est nettoyé pour garder l'app rapide."
        AppLanguage.ENGLISH -> "Auto-purge every 6 months: History older than 6 months is archived/cleaned to keep the app lightweight."
    }
    val manualPurgeAction: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fafao izao ny mihoatra ny 6 volana"
        AppLanguage.FRENCH -> "Purger l'historique de plus de 6 mois"
        AppLanguage.ENGLISH -> "Purge history older than 6 months now"
    }
    val clearAllAction: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Fafao daholo ny fandaniana rehetra"
        AppLanguage.FRENCH -> "Effacer tout l'historique"
        AppLanguage.ENGLISH -> "Clear all expense history"
    }
    val loadDemoDataAction: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Hampiditra ohatra fandaniana (Demo)"
        AppLanguage.FRENCH -> "Charger des données d'exemple (Démo)"
        AppLanguage.ENGLISH -> "Load sample demo data"
    }
    val purgeCompleted: String get() = when (lang) {
        AppLanguage.MALAGASY -> "voafafa ny fandaniana taloha"
        AppLanguage.FRENCH -> "dépenses anciennes supprimées avec succès"
        AppLanguage.ENGLISH -> "old expenses successfully purged"
    }
    val noOldDataToPurge: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tsy misy fandaniana mihoatra ny 6 volana"
        AppLanguage.FRENCH -> "Aucune dépense de plus de 6 mois trouvée"
        AppLanguage.ENGLISH -> "No expenses older than 6 months found"
    }
    val confirmPurge: String get() = when (lang) {
        AppLanguage.MALAGASY -> "Tanterahina ve ny famafana?"
        AppLanguage.FRENCH -> "Confirmer la purge ?"
        AppLanguage.ENGLISH -> "Confirm purge?"
    }

    // Month formatting
    fun formatMonthYear(yearMonth: String): String {
        return try {
            val parts = yearMonth.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            when (lang) {
                AppLanguage.MALAGASY -> {
                    val monthsMg = listOf(
                        "Janoary", "Febroary", "Martsa", "Aprily", "Mey", "Jona",
                        "Jolay", "Aogositra", "Septambra", "Oktobra", "Novambra", "Desambra"
                    )
                    "${monthsMg[month - 1]} $year"
                }
                AppLanguage.FRENCH -> {
                    val monthsFr = listOf(
                        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
                    )
                    "${monthsFr[month - 1]} $year"
                }
                AppLanguage.ENGLISH -> {
                    val monthsEn = listOf(
                        "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"
                    )
                    "${monthsEn[month - 1]} $year"
                }
            }
        } catch (e: Exception) {
            yearMonth
        }
    }

    fun formatDate(millis: Long): String {
        val date = Date(millis)
        val pattern = when (lang) {
            AppLanguage.MALAGASY -> "dd MMM yyyy"
            AppLanguage.FRENCH -> "dd/MM/yyyy"
            AppLanguage.ENGLISH -> "MMM dd, yyyy"
        }
        val locale = when (lang) {
            AppLanguage.MALAGASY -> Locale("mg")
            AppLanguage.FRENCH -> Locale.FRENCH
            AppLanguage.ENGLISH -> Locale.US
        }
        return SimpleDateFormat(pattern, locale).format(date)
    }

    companion object {
        fun formatAriary(amount: Double): String {
            val rounded = amount.toLong()
            val formatter = NumberFormat.getInstance(Locale.FRANCE)
            return "${formatter.format(rounded)} Ar"
        }
    }
}
