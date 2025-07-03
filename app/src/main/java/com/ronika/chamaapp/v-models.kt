package com.ronika.chamaapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val appDao = AppDatabase.getDatabase(application).appDao()

    // Flow to observe all users from the database
    val allUsers: Flow<List<User>> = appDao.getAllUsers() // Assuming getAllUsers() in DAO returns Flow

    fun addUser(name: String, phone: String) {
        if (name.isBlank() || phone.isBlank()) {
            return
        }
        viewModelScope.launch {
            val user = User(name = name, phone = phone)
            appDao.insertUser(user)
        }
    }

    // Optional: Function to delete a user (if you need it on this screen later)
    fun deleteUser(user: User) {
        viewModelScope.launch {
            appDao.deleteUserById(user.id) // Assuming you have deleteUserById in your DAO
        }
    }
}