package com.mehmettekin.gunkurasiapp.domain.model

data class Gold(
    val code: String,
    val name: String,
    val buyPrice: Double,
    val sellPrice: Double,
    val lastUpdated: Long
)
