package com.example.auyrma.model.entity

import com.google.gson.annotations.SerializedName

data class ClientRequest(
    @SerializedName("client_name") val clientName: String,
)
