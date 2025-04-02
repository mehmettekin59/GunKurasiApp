package com.mehmettekin.gunkurasiapp.util

object Constants {
    // API URL
    const val BASE_URL = "https://kapalicarsi.apiluna.org/"

    // Currency codes
    object CurrencyCodes {
        const val EUR = "EURTRY"
        const val USD = "USDTRY"
        const val GBP = "GBPTRY"
        const val JPY = "JPYTRY"
        const val CAD = "CADTRY"
        const val SAR = "SARTRY"

        val CURRENCY_LIST = listOf(EUR, USD, GBP, JPY, CAD, SAR)

        // Display name mappings
        val DISPLAY_NAMES = mapOf(
            EUR to "Euro (EUR)",
            USD to "Amerikan Doları (USD)",
            GBP to "İngiliz Sterlini (GBP)",
            JPY to "Japon Yeni (JPY)",
            CAD to "Kanada Doları (CAD)",
            SAR to "Suudi Riyali (SAR)"
        )

        // Get display name from code
        fun getDisplayName(code: String): String {
            return DISPLAY_NAMES[code] ?: code
        }

        // Get code from display name
        fun getCodeFromDisplayName(displayName: String): String {
            return DISPLAY_NAMES.entries.firstOrNull { it.value == displayName }?.key ?: ""
        }
    }

    // Gold codes
    object GoldCodes {
        const val CEYREK_ESKI = "CEYREK_ESKI"
        const val CEYREK_YENI = "CEYREK_YENI"
        const val YARIM_ESKI = "YARIM_ESKI"
        const val YARIM_YENI = "YARIM_YENI"
        const val TEK_ESKI = "TEK_ESKI"
        const val TEK_YENI = "TEK_YENI"
        const val ATA_ESKI = "ATA_ESKI"
        const val ATA_YENI = "ATA_YENI"
        const val ALTIN = "ALTIN"
        const val ONS = "ONS"

        val GOLD_LIST = listOf(
            CEYREK_ESKI, CEYREK_YENI,
            YARIM_ESKI, YARIM_YENI,
            TEK_ESKI, TEK_YENI,
            ATA_ESKI, ATA_YENI,
            ALTIN, ONS
        )

        // Display name mappings
        val DISPLAY_NAMES = mapOf(
            CEYREK_ESKI to "Çeyrek (Eski)",
            CEYREK_YENI to "Çeyrek (Yeni)",
            YARIM_ESKI to "Yarım (Eski)",
            YARIM_YENI to "Yarım (Yeni)",
            TEK_ESKI to "Tam (Eski)",
            TEK_YENI to "Tam (Yeni)",
            ATA_ESKI to "Ata (Eski)",
            ATA_YENI to "Ata (Yeni)",
            ALTIN to "Gram Altın",
            ONS to "ONS",
        )

        // Get display name from code
        fun getDisplayName(code: String): String {
            return DISPLAY_NAMES[code] ?: code
        }

        // Get code from display name
        fun getCodeFromDisplayName(displayName: String): String {
            return DISPLAY_NAMES.entries.firstOrNull { it.value == displayName }?.key ?: ""
        }
    }

    // DataStore keys
    object DataStoreKeys {
        const val SETTINGS_DATASTORE = "settings_datastore"
        const val API_UPDATE_INTERVAL = "api_update_interval"
        const val LANGUAGE_CODE = "language_code"
    }

    // Default settings
    object DefaultSettings {
        const val DEFAULT_API_UPDATE_INTERVAL = 30 // seconds
        const val DEFAULT_LANGUAGE = "tr" // Turkish
    }
}