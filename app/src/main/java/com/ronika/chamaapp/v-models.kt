package com.ronika.chamaapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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



class ContributionViewModel(application: Application) : AndroidViewModel(application) {

    private val appDao = AppDatabase.getDatabase(application).appDao()

    // Flow to observe all users for the dropdown
    val allUsers: Flow<List<User>> = appDao.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep active for 5s after last subscriber
            initialValue = emptyList()
        )


    // Flow to observe all users with their contributions (for ViewContributionsScreen)
    val usersWithContributions: Flow<List<UserWithContributions>> = appDao.getUsersWithContributions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addContribution(userId: Int, amount: Double, date: Long) {
        if (amount <= 0) {
            // Handle invalid amount if necessary
            return
        }
        viewModelScope.launch {
            val today = System.currentTimeMillis() // Get today's date
            val contribution = Contribution(userId = userId, amount = amount, date = today)
            appDao.insertContribution(contribution)
            // Optionally, navigate back or show a success message
        }
    }
}