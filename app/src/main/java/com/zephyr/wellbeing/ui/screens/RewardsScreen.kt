package com.zephyr.wellbeing.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun RewardsScreen(storage: AppStorage) {
    val user = storage.currentUser.value
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Badge Shop, 1: Leaderboard

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Campus Coins & Rewards", fontSize = 22.sp, fontWeight = FontWeight.Black)

        // Coin Balance Banner
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GoldCoin)
                            .padding(10.dp)
                    ) {
                        Text("🪙", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            "Your Coin Balance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            "${user?.coins ?: 0} Coins",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldCoin
                        )
                    }
                }

                if (user?.isAdmin == true) {
                    Button(
                        onClick = { storage.replenishAdminCoins() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Reset 10k", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Section Tabs
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Profile Badges Shop", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Campus Rankings", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedSection == 0) {
            // Profile Badges Coin Shop
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(storage.availableBadges) { badge ->
                    val isUnlocked = user?.unlockedBadges?.contains(badge.id) == true || user?.isAdmin == true
                    val isEquipped = user?.equippedBadge == badge.id
                    val canAfford = (user?.coins ?: 0) >= badge.cost || user?.isAdmin == true

                    val drawableId = when (badge.id) {
                        "badge_zen_scholar" -> R.drawable.badge_zen_scholar
                        "badge_time_master" -> R.drawable.badge_time_master
                        "badge_night_owl" -> R.drawable.badge_night_owl
                        else -> null
                    }

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEquipped) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isEquipped) 3.dp else 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (drawableId != null) {
                                    Image(
                                        painter = painterResource(id = drawableId),
                                        contentDescription = badge.name,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(badge.emoji, fontSize = 26.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(badge.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        if (isEquipped) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("EQUIPPED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimary)
                                            }
                                        }
                                    }
                                    Text(
                                        badge.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "Price: ${badge.cost} Coins 🪙",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldCoin
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            when {
                                isEquipped -> {
                                    OutlinedButton(
                                        onClick = { },
                                        enabled = false,
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Active", fontSize = 12.sp)
                                    }
                                }
                                isUnlocked -> {
                                    Button(
                                        onClick = { storage.equipBadge(badge.id) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Text("Equip", fontSize = 12.sp)
                                    }
                                }
                                else -> {
                                    Button(
                                        onClick = { storage.buyBadge(badge.id, badge.cost) },
                                        enabled = canAfford,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Buy", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Weekly Campus Focus Rankings
            val peers = listOf(
                Triple("Alex Chen", "28.5 hrs • 68 Sessions", "👑"),
                Triple("Maya Patel", "24.0 hrs • 58 Sessions", "🥈"),
                Triple("Jordan Taylor", "19.5 hrs • 47 Sessions", "🥉"),
                Triple("Samira Khan", "16.0 hrs • 38 Sessions", "⭐")
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(peers.size) { index ->
                    val (name, stats, badge) = peers[index]
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(badge, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(stats, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text("#${index + 1}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
