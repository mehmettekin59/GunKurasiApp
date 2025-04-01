package com.mehmettekin.gunkurasiapp.domain.model

data class DrawSettings(
    val participantCount: Int,
    val participants: List<Participant>,
    val itemType: ItemType,
    val specificItem: String,
    val monthlyAmount: Double,
    val durationMonths: Int,
    val startMonth: Int, // 1-12 for January-December
    val startYear: Int
)
