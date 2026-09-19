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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.ui.theme.GoldCoin

@Composable
fun AuthScreen(
    storage: AppStorage,
    onLoginSuccess: () -> Unit
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Create Account
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var adminPin by remember { mutableStateOf("") }
    var isAdminMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mascot Avatar
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAdminMode) "🛡️" else "📚",
                    fontSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isAdminMode) "Admin Portal" else "Zephyr Wellbeing",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (isAdminMode) "Moderator access with 10,000 auto-refreshing coins" else "Student productivity, study rooms & campus rewards",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!isAdminMode) {
                        TabRow(
                            selectedTabIndex = selectedAuthTab,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Tab(
                                selected = selectedAuthTab == 0,
                                onClick = { selectedAuthTab = 0; errorMessage = null },
                                text = { Text("Sign In", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedAuthTab == 1,
                                onClick = { selectedAuthTab = 1; errorMessage = null },
                                text = { Text("Create Account", fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isAdminMode) {
                        OutlinedTextField(
                            value = adminPin,
                            onValueChange = { if (it.length <= 6) adminPin = it },
                            label = { Text("Admin PIN (Default: 1234)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            placeholder = { Text("e.g. Alex, Maya, Sam") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            placeholder = { Text("At least 4 characters") },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = {
                            if (isAdminMode) {
                                if (storage.authenticateAdmin(adminPin)) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Invalid Admin PIN (Try 1234)"
                                }
                            } else {
                                if (selectedAuthTab == 0) {
                                    val (success, err) = storage.loginUser(username, password)
                                    if (success) onLoginSuccess() else errorMessage = err
                                } else {
                                    val (success, err) = storage.registerUser(username, password)
                                    if (success) onLoginSuccess() else errorMessage = err
                                }
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = if (isAdminMode) ButtonDefaults.buttonColors(containerColor = GoldCoin, contentColor = Color.Black)
                        else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = when {
                                isAdminMode -> "Enter Admin Portal (10,000 Coins)"
                                selectedAuthTab == 0 -> "Sign In"
                                else -> "Create Account & Get 100 Coins"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    isAdminMode = !isAdminMode
                    errorMessage = null
                },
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (isAdminMode) "Switch to Student Login" else "Admin Access (PIN 1234)")
            }
        }
    }
}
