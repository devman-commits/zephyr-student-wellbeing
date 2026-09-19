package com.zephyr.wellbeing.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.ui.theme.GoldCoin
import com.zephyr.wellbeing.ui.theme.PastelMint
import com.zephyr.wellbeing.ui.theme.PastelPeach
import com.zephyr.wellbeing.ui.theme.StreakFire

@Composable
fun AdminScreen(storage: AppStorage) {
    val user = storage.currentUser.value
    var alertText by remember { mutableStateOf("") }

    // User provisioning form state
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var initialCoinsInput by remember { mutableStateOf("100") }
    var isNewUserAdmin by remember { mutableStateOf(false) }
    var provisionMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Pod management form state
    var newPodName by remember { mutableStateOf("") }
    var newPodBuilding by remember { mutableStateOf("") }
    var podMessage by remember { mutableStateOf<String?>(null) }

    // Enforce 10,000 Coin Refresh Rule on Admin entry
    LaunchedEffect(Unit) {
        if (user?.isAdmin == true) {
            storage.replenishAdminCoins()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
            Text("Admin & Moderator Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // 10,000 Auto-Refreshing Coin Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = PastelPeach.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🪙 Admin Sandbox Balance", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldCoin)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("AUTO-REFRESH: 10,000", fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }

                Text(
                    text = "${user?.coins ?: 10000} Coins",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldCoin
                )

                Text(
                    "Admin Rule: Coins automatically replenish to 10,000 upon each login and screen visit for limitless testing of shop themes and badges.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                OutlinedButton(
                    onClick = { storage.replenishAdminCoins() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Force Refresh to 10k")
                }
            }
        }

        // Student User Provisioning Card (Creates Isolated Accounts)
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Provision New Student User", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    "Provision fresh accounts with independent credentials, isolated task checklists, and personal coin balances.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("Username") },
                    placeholder = { Text("e.g. jessica_math") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Password") },
                    placeholder = { Text("min 4 characters") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = initialCoinsInput,
                        onValueChange = { initialCoinsInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Initial Coins") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Checkbox(
                            checked = isNewUserAdmin,
                            onCheckedChange = { isNewUserAdmin = it }
                        )
                        Text("Admin", fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = {
                        val initCoins = initialCoinsInput.toIntOrNull() ?: 100
                        val (success, err) = storage.adminCreateUser(newUsername, newPassword, initCoins, isNewUserAdmin)
                        if (success) {
                            provisionMessage = Pair(true, "✅ User '$newUsername' provisioned successfully!")
                            newUsername = ""
                            newPassword = ""
                            initialCoinsInput = "100"
                            isNewUserAdmin = false
                        } else {
                            provisionMessage = Pair(false, "❌ ${err ?: "Failed to provision user"}")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Provision Account")
                }

                if (provisionMessage != null) {
                    val (isOk, msg) = provisionMessage!!
                    Text(
                        msg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOk) PastelMint else MaterialTheme.colorScheme.error
                    )
                }

                // Full Users Database Section
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Users Database (${storage.getAllUserDetails().size} Records)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelMint.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("ACTIVE SQLITE/PREFS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val allUsers = storage.getAllUserDetails()
                    allUsers.forEach { u ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = u.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (u.isAdmin) GoldCoin.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (u.isAdmin) "🛡️ Admin" else "🎓 Student",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (u.isAdmin) Color.Black else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "@${u.username}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "🪙 ${u.coins} coins  •  🔥 ${u.streakDays}d streak  •  ⏱️ ${u.totalStudyMinutes}m study",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Official Campus Pods Manager (Admin-Only)
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏛️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Official Campus Pods Manager", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    "Admin authority: create and delete official campus study pods displayed in the study room directory.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                // Pod list
                storage.officialPods.forEach { pod ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pod.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(pod.building, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        IconButton(
                            onClick = {
                                storage.removeOfficialPod(pod.id)
                                podMessage = "🗑️ Pod '${pod.name}' deleted."
                            }
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Delete Pod",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Add new pod form
                OutlinedTextField(
                    value = newPodName,
                    onValueChange = { newPodName = it },
                    label = { Text("Pod Facility Name") },
                    placeholder = { Text("e.g. Science Library Pod 5C") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = newPodBuilding,
                    onValueChange = { newPodBuilding = it },
                    label = { Text("Building & Floor") },
                    placeholder = { Text("e.g. Science Building Floor 4") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Button(
                    onClick = {
                        if (newPodName.isNotBlank() && newPodBuilding.isNotBlank()) {
                            storage.addOfficialPod(newPodName, newPodBuilding)
                            podMessage = "✅ Official Pod '${newPodName}' created!"
                            newPodName = ""
                            newPodBuilding = ""
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Official Pod")
                }

                if (podMessage != null) {
                    Text(
                        podMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Backend Server URL Configuration (Admin Gated)
        var serverInput by remember { mutableStateOf(storage.serverUrl.value) }
        var serverStatusMessage by remember { mutableStateOf<String?>(null) }

        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🖥️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Temporary Backend Server URL", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    "Configure the local FastAPI server address (default: http://10.0.2.2:8080 for emulator or your PC's LAN IP for physical phone testing).",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = serverInput,
                    onValueChange = { serverInput = it },
                    label = { Text("Server Base URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            storage.updateServerUrl(serverInput)
                            serverStatusMessage = "✅ Server URL Saved: ${storage.serverUrl.value}"
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Save URL")
                    }

                    OutlinedButton(
                        onClick = {
                            serverInput = "http://10.0.2.2:8080"
                            storage.updateServerUrl(serverInput)
                            serverStatusMessage = "🔄 Reset to Emulator Default (10.0.2.2:8080)"
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Reset Default")
                    }
                }

                if (serverStatusMessage != null) {
                    Text(
                        serverStatusMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Broadcast Alert Banner Editor
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Broadcast Campus Alert Banner", fontSize = 15.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = alertText,
                    onValueChange = { alertText = it },
                    placeholder = { Text("e.g. 📢 Library Pods now open 24/7...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            if (alertText.isNotBlank()) {
                                storage.alertBanner.value = alertText
                                alertText = ""
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Post Banner")
                    }

                    OutlinedButton(
                        onClick = { storage.alertBanner.value = null },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Clear Banner")
                    }
                }
            }
        }
    }
}
