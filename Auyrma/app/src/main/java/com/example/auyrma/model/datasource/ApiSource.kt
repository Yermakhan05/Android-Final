package com.example.auyrma.model.datasource

import com.example.auyrma.model.api.DrApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson


object ApiSource {
    private val gson = Gson()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val client: DrApi = retrofit.create(DrApi::class.java)
}