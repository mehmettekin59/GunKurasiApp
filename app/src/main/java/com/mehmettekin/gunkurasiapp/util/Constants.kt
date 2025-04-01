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

        val GOLD_LIST = listOf(
            CEYREK_ESKI, CEYREK_YENI,
            YARIM_ESKI, YARIM_YENI,
            TEK_ESKI, TEK_YENI,
            ATA_ESKI, ATA_YENI
        )
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