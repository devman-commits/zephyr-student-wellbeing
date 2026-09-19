package com.zephyr.wellbeing.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.ui.theme.StreakFire

@Composable
fun WeekdayPillBar(
    streakDays: Int = 3,
    modifier: Modifier = Modifier
) {
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val activeIndex = 3 // Wednesday active

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEachIndexed { index, day ->
                val isActive = index <= activeIndex
                val isToday = index == activeIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                        color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isToday -> StreakFire
                                    isActive -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isToday) "🔥" else (if (isActive) "✓" else "•"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
