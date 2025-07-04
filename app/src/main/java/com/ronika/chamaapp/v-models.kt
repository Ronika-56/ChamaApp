package com.ronika.chamaapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    // --- For User Selection and Displaying Contributions for ONE Selected User ---
    // (Used in UserContributionViewerScreen and AddContributionScreen for viewing existing contributions)

    /**
     * Flow to observe all users, primarily for populating dropdowns.
     * Converts to StateFlow to be lifecycle-aware and shareable.
     */
    val allUsers: StateFlow<List<User>> = appDao.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), // Keep active for 5s after last observer stops
            initialValue = emptyList()
        )

    // Internal MutableStateFlow to hold the ID of the currently selected user.
    private val _selectedUserId = MutableStateFlow<Int?>(null)
    // Public StateFlow to expose the selected user's ID (optional, if UI needs to react to just ID)
    // val selectedUserId: StateFlow<Int?> = _selectedUserId.asStateFlow()

    /**
     * Flow that emits the list of contributions for the user whose ID is in [_selectedUserId].
     * `flatMapLatest` ensures that if [_selectedUserId] changes, the previous Flow collection
     * is cancelled and a new one for the new user ID begins.
     */
    val contributionsForSelectedUser: StateFlow<List<Contribution>> =
        _selectedUserId.flatMapLatest { userId ->
            if (userId == null) {
                kotlinx.coroutines.flow.flowOf(emptyList()) // Emit empty list if no user is selected
            } else {
                appDao.getContributionsForUser(userId) // DAO method to get contributions for a specific user
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    /**
     * Called by the UI when a user is selected from a dropdown.
     * Updates the [_selectedUserId], which in turn triggers `contributionsForSelectedUser` to update.
     *
     * @param userId The ID of the user selected, or null if selection is cleared.
     */
    fun selectUser(userId: Int?) {
        _selectedUserId.value = userId
    }

    // --- For Adding a New Contribution ---
    // (Used in AddContributionScreen)

    /**
     * Adds a new contribution to the database.
     *
     * @param userId The ID of the user making the contribution.
     * @param amount The amount of the contribution.
     * @param date The date of the contribution.
     */
    fun addContribution(userId: Int, amount: Double, date: Long) {
        if (amount <= 0) {
            // Consider logging this or providing feedback to the user through a different mechanism
            return
        }
        viewModelScope.launch {
            val contribution = Contribution(userId = userId, amount = amount, date = date)
            appDao.insertContribution(contribution)
            // After adding, `contributionsForSelectedUser` will automatically update if the
            // new contribution belongs to the currently selected user, due to Room's reactive Flows.
        }
    }

    // --- For Displaying ALL Users with ALL their Contributions ---
    // (Used in a screen like ViewContributionsScreen, which lists all users and under each, their contributions)

    /**
     * Flow to observe all users along with their complete list of contributions.
     * This is useful for screens that need to display a master list.
     */
    val usersWithContributions: StateFlow<List<UserWithContributions>> =
        appDao.getUsersWithContributions() // DAO method using @Relation
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    // --- Optional: Clearing selected user ---
    // Could be useful if you want to explicitly reset the selection and the contributions list
    // fun clearSelectedUser() {
    // _selectedUserId.value = null
    // }
}