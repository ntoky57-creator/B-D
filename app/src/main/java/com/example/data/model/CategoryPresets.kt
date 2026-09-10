package com.example.data.model

data class EmojiPreset(
    val emoji: String,
    val nameEn: String,
    val nameFr: String,
    val nameMg: String
)

data class ColorPreset(
    val hex: String,
    val name: String
)

object CategoryPresets {
    val EMOJIS = listOf(
        EmojiPreset("🍔", "Food", "Restauration", "Sakafo"),
        EmojiPreset("🛒", "Groceries", "Courses", "Tsena"),
        EmojiPreset("🚗", "Transport", "Transport", "Fitaterana"),
        EmojiPreset("🏠", "Housing", "Logement", "Trano"),
        EmojiPreset("⚡", "Bills", "Factures", "Jiro sy Rano"),
        EmojiPreset("💊", "Health", "Santé", "Fahasalamana"),
        EmojiPreset("🎓", "Education", "Éducation", "Fianarana"),
        EmojiPreset("👕", "Shopping", "Shopping", "Fividianana"),
        EmojiPreset("🎬", "Entertainment", "Loisirs", "Fialam-boly"),
        EmojiPreset("✈️", "Travel", "Voyage", "Dia"),
        EmojiPreset("📱", "Phone & Internet", "Télécom & Net", "Fifandraisana"),
        EmojiPreset("☕", "Café", "Café", "Kafe"),
        EmojiPreset("🎁", "Gifts", "Cadeaux", "Fanomezana"),
        EmojiPreset("🏋️", "Fitness", "Sport", "Fanatanjahantena"),
        EmojiPreset("🐾", "Pets", "Animaux", "Biby fiompy"),
        EmojiPreset("💼", "Work & Misc", "Divers", "Asa & Hafa")
    )

    val COLORS = listOf(
        ColorPreset("#10B981", "Emerald"),
        ColorPreset("#059669", "Forest"),
        ColorPreset("#14B8A6", "Teal"),
        ColorPreset("#06B6D4", "Cyan"),
        ColorPreset("#0EA5E9", "Sky"),
        ColorPreset("#3B82F6", "Blue"),
        ColorPreset("#6366F1", "Indigo"),
        ColorPreset("#8B5CF6", "Violet"),
        ColorPreset("#A855F7", "Purple"),
        ColorPreset("#EC4899", "Pink"),
        ColorPreset("#F43F5E", "Rose"),
        ColorPreset("#EF4444", "Red"),
        ColorPreset("#F97316", "Orange"),
        ColorPreset("#F59E0B", "Amber"),
        ColorPreset("#EAB308", "Gold"),
        ColorPreset("#64748B", "Slate")
    )

    fun getDefaultCategories(lang: String = "fr"): List<CategoryEntity> {
        return EMOJIS.mapIndexed { index, preset ->
            val name = when (lang) {
                "mg" -> preset.nameMg
                "en" -> preset.nameEn
                else -> preset.nameFr
            }
            val color = COLORS[index % COLORS.size].hex
            CategoryEntity(
                name = name,
                icon = preset.emoji,
                colorHex = color,
                sortOrder = index
            )
        }
    }
}
