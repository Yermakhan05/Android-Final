package com.example.auyrma.model.entity

import com.google.gson.annotations.SerializedName

data class Pharmacy(
    @SerializedName("id") val dentistryId: Int,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("image_url") val imageUrl: String,
    val name: String,
    val address: String,
    val rating: Double,
    val email: String,
)
