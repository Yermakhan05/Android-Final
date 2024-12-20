package com.example.auyrma

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.auyrma.model.db.AppDatabase
import com.jakewharton.threetenabp.AndroidThreeTen

class MyApp : Application() {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create a new table with the updated schema
            db.execSQL("""
            CREATE TABLE sessions_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dr TEXT NOT NULL, -- Dr is stored as JSON string
                time TEXT NOT NULL,
                isNotified INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

            // Copy data from the old table to the new table
            // Assume drId will be used to create a placeholder JSON
            db.execSQL("""
            INSERT INTO sessions_new (id, dr, time, isNotified)
            SELECT id, 
                   '{"id": ' || drId || ', "name": "Unknown"}', 
                   time, 
                   isNotified 
            FROM sessions
        """.trimIndent())

            // Drop the old table
            db.execSQL("DROP TABLE sessions")

            // Rename the new table to the old table name
            db.execSQL("ALTER TABLE sessions_new RENAME TO sessions")
        }
    }



    companion object {
        private const val DATABASE_NAME = "demo_database"

        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}