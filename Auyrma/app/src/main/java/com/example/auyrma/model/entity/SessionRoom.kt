package com.example.auyrma.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dr: Dr,
    val time: String,
    val isNotified: Boolean = false
)
