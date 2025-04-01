package com.mehmettekin.gunkurasiapp.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "data") val data: T? = null,
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "error") val error: ApiError? = null
)
