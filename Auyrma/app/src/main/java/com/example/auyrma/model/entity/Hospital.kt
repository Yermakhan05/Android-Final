package com.example.auyrma.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Hospital(
    @SerializedName("id") val hospitalId: Int,
    @SerializedName("street_address") val streetAddress: String,
    @SerializedName("bed_count") val hospitalBedCount: Int,
    @SerializedName("image_url") val imageUrl: String,
    val name: String,
    val city: String,
    val rating: Double,
    val favorites: List<Int>,
    var isFavorite: Boolean = false
) : Serializable
