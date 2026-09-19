package com.zephyr.wellbeing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.ui.screens.AboutSourcesScreen
import com.zephyr.wellbeing.ui.screens.AdminScreen
import com.zephyr.wellbeing.ui.screens.AuthScreen
import com.zephyr.wellbeing.ui.screens.CampusHubScreen
import com.zephyr.wellbeing.ui.screens.HomeScreen
import com.zephyr.wellbeing.ui.screens.PlannerScreen
import com.zephyr.wellbeing.ui.screens.ProfileScreen
import com.zephyr.wellbeing.ui.screens.RewardsScreen
import com.zephyr.wellbeing.ui.screens.StudyRoomsScreen
import com.zephyr.wellbeing.ui.theme.ZephyrTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = AppStorage(applicationContext)

        setContent {
            ZephyrTheme {
                val user = storage.currentUser.value

                if (user == null) {
                    AuthScreen(storage = storage, onLoginSuccess = { /* triggers recomposition */ })
                } else {
                    val isAdminUser = user.isAdmin
                    var selectedTab by remember { mutableIntStateOf(0) }
                    var showAboutScreen by remember { mutableStateOf(false) }
                    var showProfileScreen by remember { mutableStateOf(false) }

                    // Intercept Back Button: Return to previous screen or Home instead of exiting app
                    BackHandler(enabled = showProfileScreen || showAboutScreen || (!isAdminUser && selectedTab != 0)) {
                        when {
                            showProfileScreen -> showProfileScreen = false
                            showAboutScreen -> showAboutScreen = false
                            !isAdminUser && selectedTab != 0 -> selectedTab = 0
                        }
                    }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                navigationIcon = {
                                    if (isAdminUser) {
                                        IconButton(onClick = { /* admin stays on dashboard */ }) {
                                            Text("🛡️", fontSize = 20.sp)
                                        }
                                    } else {
                                        IconButton(onClick = {
                                            showProfileScreen = !showProfileScreen
                                            showAboutScreen = false
                                        }) {
                                            val badgeEmoji = when (user.equippedBadge) {
                                                "badge_night_owl" -> "🦉"
                                                "badge_time_master" -> "⏳"
                                                "badge_zen_scholar" -> "📚"
                                                else -> "🧑‍🎓"
                                            }
                                            Text(badgeEmoji, fontSize = 20.sp)
                                        }
                                    }
                                },
                                title = {
                                    Text(
                                        text = when {
                                            isAdminUser -> if (showAboutScreen) "About & Sources" else "Admin Portal"
                                            showProfileScreen -> "Student Profile"
                                            showAboutScreen -> "About & Sources"
                                            selectedTab == 0 -> "Zephyr Wellbeing"
                                            selectedTab == 1 -> "Smart Planner"
                                            selectedTab == 2 -> "Study Rooms"
                                            selectedTab == 3 -> "Campus Hub"
                                            selectedTab == 4 -> "Rankings & Shop"
                                            else -> "Zephyr"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                },
                                actions = {
                                    if (!isAdminUser) {
                                        // Students have access to their profile
                                        IconButton(onClick = {
                                            showProfileScreen = !showProfileScreen
                                            showAboutScreen = false
                                        }) {
                                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                                        }
                                    }
                                    // Regular users DO NOT have Admin dashboard access; admin stays on dashboard
                                    IconButton(onClick = {
                                        showAboutScreen = !showAboutScreen
                                        showProfileScreen = false
                                    }) {
                                        Icon(Icons.Default.Info, contentDescription = "About")
                                    }
                                    IconButton(onClick = { storage.logout() }) {
                                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            // Bottom navigation bar is ONLY visible to students on normal tabs
                            if (!isAdminUser && !showAboutScreen && !showProfileScreen) {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = selectedTab == 0,
                                        onClick = { selectedTab = 0 },
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                        label = { Text("Home", fontSize = 11.sp) }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 1,
                                        onClick = { selectedTab = 1 },
                                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Planner") },
                                        label = { Text("Planner", fontSize = 11.sp) }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 2,
                                        onClick = { selectedTab = 2 },
                                        icon = { Icon(Icons.Default.Groups, contentDescription = "Rooms") },
                                        label = { Text("Rooms", fontSize = 11.sp) }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 3,
                                        onClick = { selectedTab = 3 },
                                        icon = { Icon(Icons.Default.School, contentDescription = "Campus") },
                                        label = { Text("Campus", fontSize = 11.sp) }
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 4,
                                        onClick = { selectedTab = 4 },
                                        icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Rankings") },
                                        label = { Text("Shop", fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AnimatedContent(
                                targetState = Triple(isAdminUser, Pair(showProfileScreen, showAboutScreen), selectedTab),
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                                },
                                label = "screenTransition"
                            ) { (adminMode, profileAbout, tab) ->
                                val (isProfile, isAbout) = profileAbout
                                when {
                                    adminMode -> {
                                        if (isAbout) {
                                            AboutSourcesScreen()
                                        } else {
                                            // Admin credentials access ONLY the admin dashboard
                                            AdminScreen(storage = storage)
                                        }
                                    }
                                    isProfile -> ProfileScreen(
                                        storage = storage,
                                        onBack = { showProfileScreen = false }
                                    )
                                    isAbout -> AboutSourcesScreen()
                                    tab == 0 -> HomeScreen(
                                        storage = storage,
                                        onNavigateToRooms = { selectedTab = 2 },
                                        onNavigateToPlanner = { selectedTab = 1 },
                                        onNavigateToProfile = { showProfileScreen = true }
                                    )
                                    tab == 1 -> PlannerScreen(storage = storage)
                                    tab == 2 -> StudyRoomsScreen(storage = storage)
                                    tab == 3 -> CampusHubScreen(storage = storage)
                                    tab == 4 -> RewardsScreen(storage = storage)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
