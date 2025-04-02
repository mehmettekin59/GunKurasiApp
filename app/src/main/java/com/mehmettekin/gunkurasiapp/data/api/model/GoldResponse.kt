package com.mehmettekin.gunkurasiapp.data.api.model

import android.util.Log
import com.mehmettekin.gunkurasiapp.domain.model.Gold
import com.mehmettekin.gunkurasiapp.util.Constants
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoldResponse(
    @Json(name = "code") val code: String,

    // Alış/satış alanları - API'de "alis" ve "satis" olarak geliyorlar
    @Json(name = "alis") val alis: Any?, // String, Double, Int olabilir
    @Json(name = "satis") val satis: Any?, // String, Double, Int olabilir

    // Yüksek/düşük/kapanış alanları
    @Json(name = "dusuk") val dusuk: Double? = null,
    @Json(name = "yuksek") val yuksek: Double? = null,
    @Json(name = "kapanis") val kapanis: Double? = null,

    // Tarih bilgisi
    @Json(name = "tarih") val tarih: String? = null
) {
    fun toDomain(): Gold {
        try {
            // Kod eşleştirmeyi saflığını artır
            val canonicalCode = Constants.GoldCodes.getCanonicalCode(code)

            // Değer dönüşümlerini güvenli yap
            val buyPrice = convertToDouble(alis)
            val sellPrice = convertToDouble(satis)
            val lastUpdated = parseDate(tarih)

            // Değerleri logla
            Log.d("GoldResponse", "Dönüştürme: $code → $canonicalCode, Alış: $alis → $buyPrice, Satış: $satis → $sellPrice")

            return Gold(
                code = canonicalCode,
                name = Constants.GoldCodes.getDisplayName(canonicalCode),
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                lastUpdated = lastUpdated
            )
        } catch (e: Exception) {
            // Hata durumunda logla
            Log.e("GoldResponse", "toDomain hatası: ${e.message}", e)
            // Varsayılan değerlerle oluştur
            return Gold(
                code = code,
                name = code,
                buyPrice = 0.0,
                sellPrice = 0.0,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    private fun convertToDouble(value: Any?): Double {
        return when (value) {
            is String -> value.toDoubleOrNull() ?: 0.0
            is Double -> value
            is Int -> value.toDouble()
            is Float -> value.toDouble()
            is Long -> value.toDouble()
            null -> 0.0
            else -> {
                Log.w("GoldResponse", "Bilinmeyen tür dönüşümü: ${value?.javaClass?.name} -> $value")
                0.0
            }
        }
    }

    private fun parseDate(dateStr: String?): Long {
        if (dateStr.isNullOrEmpty()) return System.currentTimeMillis()

        return try {
            // Örnek format: "02-04-2025 14:04:24"
            val parts = dateStr.split(" ")
            if (parts.size < 2) return System.currentTimeMillis()

            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")

            if (dateParts.size < 3 || timeParts.size < 3) return System.currentTimeMillis()

            val day = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1 // Ay 0-11 arasında
            val year = dateParts[2].toInt()

            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            val second = timeParts[2].toInt()

            java.util.Calendar.getInstance().apply {
                set(year, month, day, hour, minute, second)
            }.timeInMillis
        } catch (e: Exception) {
            Log.e("GoldResponse", "Tarih ayrıştırma hatası: $dateStr, ${e.message}")
            System.currentTimeMillis()
        }
    }
}