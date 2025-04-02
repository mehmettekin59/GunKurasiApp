package com.mehmettekin.gunkurasiapp.util

object Constants {
    // API URL - "/" soneki olmadan doğru URL
    const val BASE_URL = "https://kapalicarsi.apiluna.org"

    // Currency codes (API'den gelen kodlar)
    object CurrencyCodes {
        // Orijinal kodlar
        const val EUR = "EURTRY"
        const val USD = "USDTRY"
        const val GBP = "GBPTRY"
        const val JPY = "JPYTRY"
        const val CAD = "CADTRY"
        const val SAR = "SARTRY"

        // API yanıtındaki farklı formatlar için alternatif kodlar
        private val EUR_ALTERNATIVES = listOf("EURTRY", "EUR_TRY", "EUR-TRY", "EUR/TRY")
        private val USD_ALTERNATIVES = listOf("USDTRY", "USD_TRY", "USD-TRY", "USD/TRY")
        private val GBP_ALTERNATIVES = listOf("GBPTRY", "GBP_TRY", "GBP-TRY", "GBP/TRY")
        private val JPY_ALTERNATIVES = listOf("JPYTRY", "JPY_TRY", "JPY-TRY", "JPY/TRY")
        private val CAD_ALTERNATIVES = listOf("CADTRY", "CAD_TRY", "CAD-TRY", "CAD/TRY")
        private val SAR_ALTERNATIVES = listOf("SARTRY", "SAR_TRY", "SAR-TRY", "SAR/TRY")

        // Tüm alternatifler dahil liste
        val CURRENCY_LIST = listOf(EUR, USD, GBP, JPY, CAD, SAR)

        // Alternatifler dahil geniş liste
        val EXTENDED_CURRENCY_LIST = EUR_ALTERNATIVES + USD_ALTERNATIVES + GBP_ALTERNATIVES +
                JPY_ALTERNATIVES + CAD_ALTERNATIVES + SAR_ALTERNATIVES

        // Display name mappings
        val DISPLAY_NAMES = mapOf(
            EUR to "Euro (EUR)",
            USD to "Amerikan Doları (USD)",
            GBP to "İngiliz Sterlini (GBP)",
            JPY to "Japon Yeni (JPY)",
            CAD to "Kanada Doları (CAD)",
            SAR to "Suudi Riyali (SAR)"
        )

        // Alternatif kodları ana kodlara eşleştir
        fun getCanonicalCode(code: String): String {
            return when {
                EUR_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> EUR
                USD_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> USD
                GBP_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> GBP
                JPY_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> JPY
                CAD_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> CAD
                SAR_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> SAR
                else -> code
            }
        }

        // Get display name from code (includes alternate formats)
        fun getDisplayName(code: String): String {
            val canonicalCode = getCanonicalCode(code)
            return DISPLAY_NAMES[canonicalCode] ?: code
        }

        // Get code from display name
        fun getCodeFromDisplayName(displayName: String): String {
            return DISPLAY_NAMES.entries.firstOrNull { it.value == displayName }?.key ?: ""
        }
    }

    // Gold codes (API'den gelen kodlar)
    object GoldCodes {
        // Orijinal kodlar
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

        // API yanıtındaki farklı formatlar için alternatif kodlar
        private val CEYREK_ESKI_ALTERNATIVES = listOf("CEYREK_ESKI", "CEYREKESKI", "CEYREK-ESKI", "CEYREK ESKI", "CEYREK ALTIN ESKI", "CEYREKALTIN_ESKI")
        private val CEYREK_YENI_ALTERNATIVES = listOf("CEYREK_YENI", "CEYREKYENI", "CEYREK-YENI", "CEYREK YENI", "CEYREK ALTIN YENI", "CEYREKALTIN_YENI")
        private val YARIM_ESKI_ALTERNATIVES = listOf("YARIM_ESKI", "YARIMESKI", "YARIM-ESKI", "YARIM ESKI", "YARIM ALTIN ESKI", "YARIMALTIN_ESKI")
        private val YARIM_YENI_ALTERNATIVES = listOf("YARIM_YENI", "YARIMYENI", "YARIM-YENI", "YARIM YENI", "YARIM ALTIN YENI", "YARIMALTIN_YENI")
        private val TEK_ESKI_ALTERNATIVES = listOf("TEK_ESKI", "TEKESKI", "TEK-ESKI", "TEK ESKI", "TAM ESKI", "TAM_ESKI", "TAMALTIN_ESKI")
        private val TEK_YENI_ALTERNATIVES = listOf("TEK_YENI", "TEKYENI", "TEK-YENI", "TEK YENI", "TAM YENI", "TAM_YENI", "TAMALTIN_YENI")
        private val ATA_ESKI_ALTERNATIVES = listOf("ATA_ESKI", "ATAESKI", "ATA-ESKI", "ATA ESKI", "ATA ALTIN ESKI", "ATAALTIN_ESKI")
        private val ATA_YENI_ALTERNATIVES = listOf("ATA_YENI", "ATAYENI", "ATA-YENI", "ATA YENI", "ATA ALTIN YENI", "ATAALTIN_YENI")
        private val ALTIN_ALTERNATIVES = listOf("ALTIN", "GRAM_ALTIN", "GRAMALTIN", "GRAM-ALTIN", "GRAM ALTIN", "GRALTIN", "HAS ALTIN")
        private val ONS_ALTERNATIVES = listOf("ONS", "ONS_ALTIN", "ONSALTIN", "ONS-ALTIN", "ONS ALTIN", "ONALTIN")

        val GOLD_LIST = listOf(
            CEYREK_ESKI, CEYREK_YENI,
            YARIM_ESKI, YARIM_YENI,
            TEK_ESKI, TEK_YENI,
            ATA_ESKI, ATA_YENI,
            ALTIN, ONS
        )

        // Alternatifler dahil geniş liste
        val EXTENDED_GOLD_LIST = CEYREK_ESKI_ALTERNATIVES + CEYREK_YENI_ALTERNATIVES +
                YARIM_ESKI_ALTERNATIVES + YARIM_YENI_ALTERNATIVES +
                TEK_ESKI_ALTERNATIVES + TEK_YENI_ALTERNATIVES +
                ATA_ESKI_ALTERNATIVES + ATA_YENI_ALTERNATIVES +
                ALTIN_ALTERNATIVES + ONS_ALTERNATIVES

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

        // Alternatif kodları ana kodlara eşleştir
        fun getCanonicalCode(code: String): String {
            return when {
                CEYREK_ESKI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> CEYREK_ESKI
                CEYREK_YENI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> CEYREK_YENI
                YARIM_ESKI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> YARIM_ESKI
                YARIM_YENI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> YARIM_YENI
                TEK_ESKI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> TEK_ESKI
                TEK_YENI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> TEK_YENI
                ATA_ESKI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> ATA_ESKI
                ATA_YENI_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> ATA_YENI
                ALTIN_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> ALTIN
                ONS_ALTERNATIVES.any { it.equals(code, ignoreCase = true) } -> ONS
                else -> code
            }
        }

        // Get display name from code (includes alternate formats)
        fun getDisplayName(code: String): String {
            val canonicalCode = getCanonicalCode(code)
            return DISPLAY_NAMES[canonicalCode] ?: code
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