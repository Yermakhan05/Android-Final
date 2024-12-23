package com.example.auyrma.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Dr(
    val id: Int,
    @SerializedName("medic_name") val name: String,
    @SerializedName("medic_image") val medicImage: String,
    val speciality: String,
    val price: Int,
    val hospital: Int = 0,
    val favorites: List<Int> = listOf(),
    var isFavorite: Boolean = false
): Serializable

