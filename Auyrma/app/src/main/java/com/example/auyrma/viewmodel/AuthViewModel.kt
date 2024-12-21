package com.example.auyrma.viewmodel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auyrma.model.entity.User
import com.example.auyrma.model.repository.UserRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    // Метод для регистрации пользователя
    suspend fun registerUser(user: User): Boolean {
        return if (!repository.isUserExists(user.phone)) {
            repository.insertUser(user)
            true // Регистрация успешна
        } else {
            false // Пользователь с таким номером уже существует
        }
    }
    fun saveAuthState(userId: Int) {
        repository.saveAuthState(userId)
    }

    fun isUserAuthenticated(): Boolean {
        return repository.isUserAuthenticated()
    }

    fun getAuthenticatedUserId(): Int {
        return repository.getAuthenticatedUserId()
    }

    fun logout() {
        repository.clearAuthState()
    }
    suspend fun loginUser(phone: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            repository.getUser(phone, password)
        }
    }

    suspend fun getUserById(userId: Int): User? {
        return repository.getUserById(userId)
    }


}