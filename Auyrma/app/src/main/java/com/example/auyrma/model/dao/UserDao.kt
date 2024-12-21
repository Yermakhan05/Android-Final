package com.example.auyrma.model.dao



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.auyrma.model.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)


    @Query("SELECT * FROM users WHERE phone = :phone AND password = :password LIMIT 1")
    suspend fun getUser(phone: String, password: String): User?
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getUserByName(name: String): User?

    @Query("SELECT * FROM users WHERE city = :city LIMIT 1")
    suspend fun getUserByCity(city: String): User?

}