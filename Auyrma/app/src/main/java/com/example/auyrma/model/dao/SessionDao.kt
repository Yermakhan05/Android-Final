package com.example.auyrma.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.auyrma.model.entity.SessionRoom

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionRoom)

    @Query("SELECT * FROM sessions WHERE isNotified = 0 ORDER BY time ASC")
    suspend fun getUpcomingSessions(): List<SessionRoom>

    @Update
    suspend fun updateSession(session: SessionRoom)
}