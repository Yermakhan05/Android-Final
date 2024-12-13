package com.example.auyrma.model.entity
import com.google.gson.annotations.SerializedName


data class DrSession(
    val id: Int,
    @SerializedName("medic_name") val name: String,
    @SerializedName("medic_image") val medicImage: String,
    val speciality: String,
    val price: Int,
    val hospital: Int = 0,
    val favorites: List<Int> = listOf(),
    var isFavorite: Boolean = false
)
