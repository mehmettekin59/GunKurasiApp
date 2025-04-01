package com.mehmettekin.gunkurasiapp.domain.model

data class Participant(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String
)
