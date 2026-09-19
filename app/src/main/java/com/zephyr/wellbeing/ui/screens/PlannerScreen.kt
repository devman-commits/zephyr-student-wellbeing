package com.zephyr.wellbeing.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.ui.theme.GoldCoin
import com.zephyr.wellbeing.ui.theme.PastelMint
import kotlinx.coroutines.delay

@Composable
fun PlannerScreen(storage: AppStorage) {
    var selectedDurationMinutes by remember { mutableIntStateOf(25) }
    var timerSeconds by remember { mutableIntStateOf(25 * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskCategory by remember { mutableStateOf("Logic") }

    val rewardCoins = maxOf(1, selectedDurationMinutes / 3)

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
            if (timerSeconds == 0) {
                isTimerRunning = false
                storage.recordPomodoroCompletion(selectedDurationMinutes)
            }
        }
    }

    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pomodoro Focus Header Card
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Smart Pomodoro Timer",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GoldCoin)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+$rewardCoins Coins (1 coin / 3 min)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Duration Selector Pill Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(15, 25, 30, 45, 60).forEach { mins ->
                            val isSelected = selectedDurationMinutes == mins
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                    )
                                    .clickable(enabled = !isTimerRunning) {
                                        selectedDurationMinutes = mins
                                        timerSeconds = mins * 60
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${mins}m",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = formattedTime,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(if (isTimerRunning) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTimerRunning) "Pause" else "Start Focus")
                        }

                        Button(
                            onClick = {
                                isTimerRunning = false
                                timerSeconds = selectedDurationMinutes * 60
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Tasks List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Study Checklist",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "${storage.tasks.count { it.isCompleted }}/${storage.tasks.size} Done",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Tasks LazyColumn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(storage.tasks, key = { it.id }) { task ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { storage.toggleTask(task.id) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(task.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(task.deadline, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            IconButton(
                                onClick = { storage.removeTask(task.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Remove Task",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Task FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }

        if (showAddDialog) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("New Study Goal / Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Task Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskCategory,
                        onValueChange = { newTaskCategory = it },
                        label = { Text("Category (e.g. Logic, Visual, Focus)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showAddDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    storage.addTask(newTaskTitle, newTaskCategory, "Today")
                                    newTaskTitle = ""
                                    showAddDialog = false
                                }
                            }
                        ) {
                            Text("Save Task")
                        }
                    }
                }
            }
        }
    }
}
