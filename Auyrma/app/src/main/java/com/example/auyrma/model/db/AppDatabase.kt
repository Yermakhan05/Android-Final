package com.example.auyrma.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.auyrma.model.dao.SessionDao
import com.example.auyrma.model.entity.SessionRoom

@Database(entities = [SessionRoom::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}