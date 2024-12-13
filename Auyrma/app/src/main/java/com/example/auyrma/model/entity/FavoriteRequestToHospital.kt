package com.example.auyrma.model.entity

import com.google.gson.annotations.SerializedName

data class FavoriteRequestToHospital(
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("hospital_id") val hospitalId: Int
)
