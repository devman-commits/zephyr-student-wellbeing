package com.zephyr.wellbeing.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.R
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.ui.theme.GoldCoin
import com.zephyr.wellbeing.ui.theme.PastelMint
import com.zephyr.wellbeing.ui.theme.PastelPeach
import com.zephyr.wellbeing.ui.theme.StreakFire

@Composable
fun ProfileScreen(
    storage: AppStorage,
    onBack: () -> Unit
) {
    val user = storage.currentUser.value
    var editDisplayName by remember(user?.displayName) { mutableStateOf(user?.displayName ?: "") }
    var showSavedMessage by remember { mutableStateOf(false) }

    val badgeDrawable = when (user?.equippedBadge) {
        "badge_zen_scholar" -> R.drawable.badge_zen_scholar
        "badge_time_master" -> R.drawable.badge_time_master
        "badge_night_owl" -> R.drawable.badge_night_owl
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Student Profile & Identity",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Avatar & Role Identity Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Large Avatar with Equipped Badge Artwork
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(3.dp, if (user?.isAdmin == true) GoldCoin else MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (badgeDrawable != null) {
                        Image(
                            painter = painterResource(id = badgeDrawable),
                            contentDescription = user?.equippedBadge,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = if (user?.isAdmin == true) "🛡️" else "🧑‍🎓",
                            fontSize = 48.sp
                        )
                    }
                }

                Text(
                    text = user?.displayName ?: "Scholar",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "@${user?.username ?: "student"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.outline
                )

                // Role Badge (Student vs Admin)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (user?.isAdmin == true) GoldCoin else MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (user?.isAdmin == true) "🛡️ CAMPUS ADMINISTRATOR" else "🎓 VERIFIED STUDENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user?.isAdmin == true) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Editable Display Name Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Edit Display Name",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editDisplayName,
                        onValueChange = {
                            editDisplayName = it
                            showSavedMessage = false
                        },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Button(
                        onClick = {
                            if (editDisplayName.isNotBlank()) {
                                storage.updateDisplayName(editDisplayName)
                                showSavedMessage = true
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                }

                if (showSavedMessage) {
                    Text(
                        text = "✅ Display name saved successfully!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Statistics Cards (2x2 Grid)
        Text(
            text = "Personal Wellbeing Metrics",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stat 1: Total Coins
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelPeach.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🪙 Total Coins", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${user?.coins ?: 0}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldCoin
                    )
                }
            }

            // Stat 2: Current Streak
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PastelMint.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🔥 Daily Streak", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${user?.streakDays ?: 0} Days",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = StreakFire
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stat 3: Total Study Minutes
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("⏱️ Total Study Time", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "${user?.totalStudyMinutes ?: 0} min",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Stat 4: Completed Pomodoros
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🍅 Pomodoros Done", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "${user?.completedPomodoros ?: 0}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Quick Badge Switcher
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Equip Unlocked Badge",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                val unlockedBadges = storage.availableBadges.filter {
                    user?.unlockedBadges?.contains(it.id) == true || user?.isAdmin == true
                }

                if (unlockedBadges.isEmpty()) {
                    Text(
                        text = "No badges unlocked yet. Visit the Shop to unlock badges with Campus Coins!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    unlockedBadges.forEach { badge ->
                        val isEquipped = user?.equippedBadge == badge.id
                        val dId = when (badge.id) {
                            "badge_zen_scholar" -> R.drawable.badge_zen_scholar
                            "badge_time_master" -> R.drawable.badge_time_master
                            "badge_night_owl" -> R.drawable.badge_night_owl
                            else -> null
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isEquipped) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background)
                                .clickable { storage.equipBadge(badge.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (dId != null) {
                                    Image(
                                        painter = painterResource(id = dId),
                                        contentDescription = badge.name,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Text(badge.emoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(badge.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(badge.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, maxLines = 1)
                                }
                            }

                            if (isEquipped) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("EQUIPPED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { storage.equipBadge(badge.id) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Equip", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Direct Logout Button
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Account Session",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { storage.logout() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out from @${user?.username ?: "account"}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
