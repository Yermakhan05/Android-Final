package com.example.auyrma.model.entity

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("medic_id") val medicId: Int
)

data class FavoriteResponse(
    val message: String,
    val error: String? = null
)