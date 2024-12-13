package com.example.auyrma.model.entity

data class Session(
    val id: Int,
    val medics: Dr,
    val client: Int,
    val appointment: String
)