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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.ui.components.ProgressDonutArc
import com.zephyr.wellbeing.ui.components.WeekdayPillBar
import com.zephyr.wellbeing.ui.theme.GoldCoin
import com.zephyr.wellbeing.ui.theme.PastelMint
import com.zephyr.wellbeing.ui.theme.PastelPeach
import com.zephyr.wellbeing.ui.theme.StreakFire

@Composable
fun HomeScreen(
    storage: AppStorage,
    onNavigateToRooms: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    val user = storage.currentUser.value
    val alert = storage.alertBanner.value
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Logic", "Visual", "Focus", "Tech")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Profile Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToProfile() }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val badgeEmoji = when (user?.equippedBadge) {
                        "badge_night_owl" -> "🦉"
                        "badge_time_master" -> "⏳"
                        "badge_zen_scholar" -> "📚"
                        else -> if (user?.isAdmin == true) "🛡️" else "🧑‍🎓"
                    }
                    Text(badgeEmoji, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hello, ${user?.displayName ?: "Scholar"}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1
                        )
                        if (user?.isAdmin == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldCoin)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("ADMIN", fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    val badgeLabel = when (user?.equippedBadge) {
                        "badge_night_owl" -> "🦉 Night Owl Scholar"
                        "badge_time_master" -> "⏳ Time Master"
                        "badge_zen_scholar" -> "📚 Zen Scholar"
                        else -> if (user?.isAdmin == true) "Campus Supervisor" else "Focus Scholar"
                    }
                    Text(
                        text = badgeLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Streak & Coins Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.coins ?: 0}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldCoin,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.streakDays ?: 0}d",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = StreakFire,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // Campus Broadcast Alert Banner (if present)
        if (alert != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = alert,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // "Your Progress Today" Card with Donut Arc Gauge
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        text = "Your Progress Today",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "July 2026",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Half-Donut Score Arc (recreates reference mockup score 200)
                ProgressDonutArc(score = 200 + ((user?.coins ?: 0) / 50))

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("12", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Achieved", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${user?.totalStudyMinutes ?: 0}m", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Focus Time", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${user?.completedPomodoros ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Pomodoros", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        // Weekday Streak Selector Pill Bar
        WeekdayPillBar(streakDays = user?.streakDays ?: 1)

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                SuggestionChip(
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            filter,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Two-Column Pastel Routine & Room Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: Focus Pomodoro
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PastelMint.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⏱️", fontSize = 18.sp)
                    }
                    Column {
                        Text("Focus Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("25m Pomodoro block", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Button(
                        onClick = onNavigateToPlanner,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Timer", fontSize = 12.sp)
                    }
                }
            }

            // Card 2: Encrypted Study Rooms
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔒", fontSize = 18.sp)
                    }
                    Column {
                        Text("Study Rooms", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("AES-256 Peer Chat", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Button(
                        onClick = onNavigateToRooms,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Join Room", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
