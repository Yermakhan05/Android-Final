package com.example.auyrma.model.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.auyrma.model.dao.SessionDao
import com.example.auyrma.model.dao.UserDao
import com.example.auyrma.model.entity.SessionRoom
import com.example.auyrma.model.entity.User


@Database(entities = [SessionRoom::class, User::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun sessionDao(): SessionDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 2 to 3: Ensure "users" table is added
//        private val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("""
//                    CREATE TABLE IF NOT EXISTS `users` (
//                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                        `name` TEXT NOT NULL,
//                        `phone` TEXT NOT NULL,
//                        `password` TEXT NOT NULL,
//                        `city` TEXT NOT NULL,
//                        `balance` REAL NOT NULL DEFAULT 1000.0
//                    )
//                """.trimIndent())
//            }
//        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medical_app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

