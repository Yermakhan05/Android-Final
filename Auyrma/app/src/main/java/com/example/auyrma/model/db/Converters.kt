package com.example.auyrma.model.db
import androidx.room.TypeConverter
import com.example.auyrma.model.entity.Dr
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDr(dr: Dr): String {
        return gson.toJson(dr)
    }

    @TypeConverter
    fun toDr(drString: String): Dr {
        val type = object : TypeToken<Dr>() {}.type
        return gson.fromJson(drString, type)
    }
}