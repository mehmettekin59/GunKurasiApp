package com.mehmettekin.gunkurasiapp.domain.model

data class DrawResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val participantId: String,
    val participantName: String,
    val month: String,
    val amount: String,
    val date: Long = System.currentTimeMillis()
)
