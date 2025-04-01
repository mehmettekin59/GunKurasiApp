package com.mehmettekin.gunkurasiapp.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiError(
    @Json(name = "message") val message: String? = null,
    @Json(name = "status") val status: Int? = null,
    @Json(name = "code") val code: String? = null
)
