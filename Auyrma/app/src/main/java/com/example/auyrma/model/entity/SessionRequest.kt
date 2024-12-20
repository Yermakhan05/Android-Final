package com.example.auyrma.model.entity

import com.google.gson.annotations.SerializedName

data class SessionRequest(
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("medics_id") val medicsId: Int,
    val appointment: String
)
