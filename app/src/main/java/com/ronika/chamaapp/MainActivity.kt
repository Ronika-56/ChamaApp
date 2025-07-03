package com.ronika.chamaapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ronika.chamaapp.ui.theme.ChamaAppTheme
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChamaAppTheme {
                val navController = rememberNavController()
                AppNavigator(navController = navController)
            }
        }
    }
}

object AppDestinations {
    const val HOME_SCREEN = "home"
    const val ADD_USER_SCREEN = "addUser"
    const val ADD_CONTRIBUTION_SCREEN = "addContribution"
    const val VIEW_USERS_SCREEN = "viewUsers"
    const val VIEW_CONTRIBUTIONS_SCREEN = "viewContributions"
}

@Composable
fun AppNavigator(
    navController: NavHostController,
    modifier: Modifier = Modifier // It's good practice to include a modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_SCREEN,
        modifier = modifier // Apply the modifier to the NavHost
    ) {
        composable(AppDestinations.HOME_SCREEN) {
            HomeScreen(
                onNavigateToAddUser = { navController.navigate(AppDestinations.ADD_USER_SCREEN) },
                onNavigateToAddContribution = { navController.navigate(AppDestinations.ADD_CONTRIBUTION_SCREEN) },
                onNavigateToViewUsers = { navController.navigate(AppDestinations.VIEW_USERS_SCREEN) },
                onNavigateToViewContributions = { navController.navigate(AppDestinations.VIEW_CONTRIBUTIONS_SCREEN) }
            )
        }
        composable(AppDestinations.ADD_USER_SCREEN) {
            AddUserScreen(onNavigateUp = { navController.popBackStack() })
        }
        composable(AppDestinations.ADD_CONTRIBUTION_SCREEN) {
            AddContributionScreen(onNavigateUp = { navController.popBackStack() })
        }
        composable(AppDestinations.VIEW_USERS_SCREEN) {
            ViewUsersScreen(onNavigateUp = { navController.popBackStack() })
        }
        composable(AppDestinations.VIEW_CONTRIBUTIONS_SCREEN) {
            ViewContributionsScreen(onNavigateUp = { navController.popBackStack() })
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddUser: () -> Unit,
    onNavigateToAddContribution: () -> Unit,
    onNavigateToViewUsers: () -> Unit,
    onNavigateToViewContributions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chama App Home") })
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = onNavigateToAddUser, modifier = Modifier.width(200.dp)) {
                Text("Add User")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToAddContribution, modifier = Modifier.width(200.dp)) {
                Text("Add Contribution")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToViewUsers, modifier = Modifier.width(200.dp)) {
                Text("View Users")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToViewContributions, modifier = Modifier.width(200.dp)) {
                Text("View Contributions")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel() // Inject ViewModel
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val context = LocalContext.current // For showing Toasts

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New User") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp), // Add some padding around the content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Add spacing between elements
        ) {
            Text(
                "Enter User Details",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        userViewModel.addUser(name, phone)
                        Toast.makeText(context, "User Saved!", Toast.LENGTH_SHORT).show()
                        // Optionally clear fields or navigate up
                        name = ""
                        phone = ""
                        onNavigateUp() // Navigate back after saving
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save User")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContributionScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    contributionViewModel: ContributionViewModel = viewModel()
) {
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var amountString by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val usersList by contributionViewModel.allUsers.collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Contribution") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Enter Contribution Details",
                style = MaterialTheme.typography.headlineSmall
            )

            // User Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedUser?.name ?: "Select User",
                    onValueChange = { /* Read only */ },
                    label = { Text("User") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown)
                    },
                    modifier = Modifier
                        .menuAnchor() // Important for proper positioning
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (usersList.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No users found. Add users first.") },
                            onClick = { expandedDropdown = false },
                            enabled = false
                        )
                    }
                    usersList.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                selectedUser = user
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            // Amount TextField
            OutlinedTextField(
                value = amountString,
                onValueChange = { input ->
                    // Allow only numbers and a single decimal point
                    if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        amountString = input
                    }
                },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    val amount = amountString.toDoubleOrNull()
                    if (selectedUser == null) {
                        Toast.makeText(context, "Please select a user", Toast.LENGTH_SHORT).show()
                    } else if (amount == null || amount <= 0) {
                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                    } else {
                        contributionViewModel.addContribution(
                            userId = selectedUser!!.id, // selectedUser is guaranteed not null here
                            amount = amount,
                            date = System.currentTimeMillis() // Use current date, or implement a date picker
                        )
                        Toast.makeText(context, "Contribution Saved!", Toast.LENGTH_SHORT).show()
                        // Optionally clear fields or navigate up
                        selectedUser = null
                        amountString = ""
                        onNavigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = usersList.isNotEmpty() // Disable button if no users to select from
            ) {
                Text("Save Contribution")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewUsersScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel() // Inject ViewModel
) {
    // Collect the list of users as state. Recomposes when the list changes.
    val usersListState by userViewModel.allUsers.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("View Users") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // Add horizontal padding for the list items
        ) {
            if (usersListState.isEmpty()) {
                // Show a message or a loading indicator if the list is empty
                // For a loading state, you might have a separate boolean state in your ViewModel
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found.", style = MaterialTheme.typography.bodyLarge)
                    // Or if you have a proper loading state:
                    // if (isLoading) { CircularProgressIndicator() } else { Text("No users found.") }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
                ) {
                    items(
                        items = usersListState,
                        key = { user -> user.id } // Provide a stable key for better performance
                    ) { user ->
                        UserItem(
                            user = user,
                            onUserClick = {
                                // Handle user click, e.g., navigate to a user detail screen
                                // For now, let's just log or Toast
                                println("Clicked user: ${user.name}")
                            },
                            onDeleteUser = {
                                userViewModel.deleteUser(user) // Example: delete user
                            }
                        )
                        HorizontalDivider() // Optional: Adds a line between items
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onUserClick: (User) -> Unit,
    onDeleteUser: (User) -> Unit, // Callback for delete
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onUserClick(user) }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) { // Allow text to take available space
                Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "ID: ${user.id}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Phone: ${user.phone}", style = MaterialTheme.typography.bodySmall)
            }
            // Example: Delete button
            IconButton(onClick = { onDeleteUser(user) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete User",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewContributionsScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("View Contributions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Add Contribution Screen Content", style = MaterialTheme.typography.headlineSmall)
            // TODO: Add fields for userId (perhaps a dropdown), amount, date and a Save Button
        }
    }
}
