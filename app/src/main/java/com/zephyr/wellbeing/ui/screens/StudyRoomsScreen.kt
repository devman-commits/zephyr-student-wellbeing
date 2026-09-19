package com.zephyr.wellbeing.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.data.ChatMessage
import com.zephyr.wellbeing.data.CryptoUtils
import com.zephyr.wellbeing.data.StudyRoom
import com.zephyr.wellbeing.ui.theme.GoldCoin
import com.zephyr.wellbeing.ui.theme.PastelMint
import com.zephyr.wellbeing.ui.theme.PastelPeach
import com.zephyr.wellbeing.ui.theme.StreakFire
import kotlinx.coroutines.delay

@Composable
fun StudyRoomsScreen(storage: AppStorage) {
    val activeRoom = storage.activeRoom.value
    var inputPasskey by remember { mutableStateOf("STU123") }
    var outgoingMessage by remember { mutableStateOf("") }
    var showCreateRoomDialog by remember { mutableStateOf(false) }

    // Dialog form state for custom room creation
    var newRoomName by remember { mutableStateOf("") }
    var newRoomPasskey by remember { mutableStateOf("") }
    var newRoomDuration by remember { mutableIntStateOf(25) }

    if (activeRoom == null) {
        // Room Browser & Passkey Entry View
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Collaborative Study Rooms",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "End-to-end encrypted peer rooms with synchronized host timer.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Button(
                        onClick = { showCreateRoomDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create", fontSize = 13.sp)
                    }
                }

                // Passkey Direct Join Box
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Quick Passkey Join", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = inputPasskey,
                                onValueChange = { if (it.length <= 6) inputPasskey = it.uppercase() },
                                placeholder = { Text("e.g. STU123") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            Button(
                                onClick = {
                                    val matched = storage.rooms.firstOrNull { it.passkey.equals(inputPasskey.trim(), ignoreCase = true) }
                                    if (matched != null) {
                                        storage.activeRoom.value = matched
                                    } else {
                                        // Create ad-hoc room with entered passkey
                                        val adhoc = storage.createStudyRoom("Room ${inputPasskey.trim()}", inputPasskey.trim(), 25)
                                        storage.activeRoom.value = adhoc
                                    }
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Join")
                            }
                        }
                    }
                }

                Text("Available Campus Pods & Peer Rooms", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(storage.rooms) { room ->
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(room.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (room.isOfficialPod) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(PastelMint.copy(alpha = 0.5f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("🏛️ Official Campus Pod", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(PastelPeach.copy(alpha = 0.5f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("Host: @${room.creatorUsername}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE65100))
                                            }
                                        }

                                        Text("⏱️ ${room.timerDurationMinutes}m", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        Text("👥 ${room.participantsCount} peers", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }

                                Button(
                                    onClick = {
                                        inputPasskey = room.passkey
                                        storage.activeRoom.value = room
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Join")
                                }
                            }
                        }
                    }
                }
            }

            // Create Room Dialog
            if (showCreateRoomDialog) {
                Dialog(onDismissRequest = { showCreateRoomDialog = false }) {
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                "Create Custom Study Room",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                "You will be the Host and can control the shared timer for all joining peers.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )

                            OutlinedTextField(
                                value = newRoomName,
                                onValueChange = { newRoomName = it },
                                label = { Text("Room Name") },
                                placeholder = { Text("e.g. Distributed Systems Focus") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            )

                            OutlinedTextField(
                                value = newRoomPasskey,
                                onValueChange = { if (it.length <= 6) newRoomPasskey = it.uppercase() },
                                label = { Text("6-Char Passkey (AES Key)") },
                                placeholder = { Text("e.g. DIST99") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Text("Pomodoro Timer Length", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(15, 25, 30, 45, 60).forEach { mins ->
                                    val isSelected = newRoomDuration == mins
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { newRoomDuration = mins }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${mins}m",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { showCreateRoomDialog = false },
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Cancel")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val name = if (newRoomName.isBlank()) "Peer Focus Pod" else newRoomName.trim()
                                        val pass = if (newRoomPasskey.isBlank()) "ROOM88" else newRoomPasskey.trim().uppercase()
                                        val created = storage.createStudyRoom(name, pass, newRoomDuration)
                                        inputPasskey = pass
                                        storage.activeRoom.value = created
                                        showCreateRoomDialog = false
                                        newRoomName = ""
                                        newRoomPasskey = ""
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Create & Host")
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Active Encrypted Chat & Shared Host Timer Room View
        val currentUser = storage.currentUser.value
        val isCreator = (activeRoom.creatorUsername == (currentUser?.username ?: "")) || (currentUser?.isAdmin == true)

        var roomTimerSeconds by remember(activeRoom.id) { mutableIntStateOf(activeRoom.pomodoroSecondsRemaining) }
        var isTimerActive by remember(activeRoom.id) { mutableStateOf(activeRoom.isTimerRunning) }

        LaunchedEffect(isTimerActive, activeRoom.id) {
            while (isTimerActive && roomTimerSeconds > 0) {
                delay(1000L)
                roomTimerSeconds--
                if (roomTimerSeconds == 0) {
                    isTimerActive = false
                    storage.recordPomodoroCompletion(activeRoom.timerDurationMinutes)
                }
            }
        }

        val timerMinutes = roomTimerSeconds / 60
        val timerSecs = roomTimerSeconds % 60
        val formattedTimer = String.format("%02d:%02d", timerMinutes, timerSecs)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Room Header with Host Attribution and Leave Button
            Card(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(activeRoom.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                if (activeRoom.isOfficialPod) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PastelMint)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Official Pod", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    }
                                }
                            }
                            Text(
                                "Host: @${activeRoom.creatorUsername} • Passkey: $inputPasskey",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Button(
                            onClick = { storage.activeRoom.value = null },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Leave", fontSize = 12.sp)
                        }
                    }

                    // Shared Pomodoro Timer Card with Host-Only Controls
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text(
                                        "Shared Focus Timer (${activeRoom.timerDurationMinutes}m)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                if (isCreator) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GoldCoin)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("👑 HOST CONTROLS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("⏱️ Host Controlled", fontSize = 9.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Text(
                                text = formattedTimer,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            if (isCreator) {
                                // Creator controls: Start / Pause / Reset
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = { isTimerActive = !isTimerActive },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(if (isTimerActive) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isTimerActive) "Pause" else "Start Timer", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            isTimerActive = false
                                            roomTimerSeconds = activeRoom.timerDurationMinutes * 60
                                        },
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reset", fontSize = 11.sp)
                                    }
                                }
                            } else {
                                // Non-creators see synchronized status banner
                                Text(
                                    "⏱️ Timer synchronized with host (@${activeRoom.creatorUsername})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Ciphertext Inspector Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (storage.showCiphertext.value) "🚨 Ciphertext Inspector (Raw Wire)" else "🔒 AES-256 Decrypted View",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (storage.showCiphertext.value) StreakFire else MaterialTheme.colorScheme.primary
                        )
                        Switch(
                            checked = storage.showCiphertext.value,
                            onCheckedChange = { storage.showCiphertext.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = StreakFire)
                        )
                    }
                }
            }

            // Message Stream
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(storage.roomMessages) { msg ->
                    val isMe = msg.sender == (storage.currentUser.value?.displayName ?: "Scholar")
                    val displayedText = if (storage.showCiphertext.value) {
                        msg.ciphertext
                    } else {
                        CryptoUtils.decrypt(msg.ciphertext, inputPasskey)
                    }

                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(msg.sender, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isMe) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = displayedText,
                                fontSize = if (storage.showCiphertext.value) 10.sp else 13.sp,
                                fontFamily = if (storage.showCiphertext.value) FontFamily.Monospace else FontFamily.Default,
                                color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Chat Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = outgoingMessage,
                    onValueChange = { outgoingMessage = it },
                    placeholder = { Text("Send message...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (outgoingMessage.isNotBlank()) {
                            val encrypted = CryptoUtils.encrypt(outgoingMessage, inputPasskey)
                            val sender = storage.currentUser.value?.displayName ?: "Scholar"
                            storage.roomMessages.add(ChatMessage("msg_${System.currentTimeMillis()}", sender, encrypted))
                            outgoingMessage = ""
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Send")
                }
            }
        }
    }
}
