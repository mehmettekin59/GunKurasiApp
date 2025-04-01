package com.mehmettekin.gunkurasiapp.data.api.model

import com.mehmettekin.gunkurasiapp.domain.model.Gold
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoldResponse(
    @Json(name = "code") val code: String,
    @Json(name = "name") val name: String,
    @Json(name = "buy") val buy: Double,
    @Json(name = "sell") val sell: Double,
    @Json(name = "updated_at") val updatedAt: Long
) {
    fun toDomain(): Gold {
        return Gold(
            code = code,
            name = name,
            buyPrice = buy,
            sellPrice = sell,
            lastUpdated = updatedAt
        )
    }
}
