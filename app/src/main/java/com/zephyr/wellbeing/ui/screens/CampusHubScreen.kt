package com.zephyr.wellbeing.ui.screens

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.data.AppStorage
import com.zephyr.wellbeing.data.BookingPass
import com.zephyr.wellbeing.ui.theme.PastelMint
import com.zephyr.wellbeing.ui.theme.PastelPeach

@Composable
fun CampusHubScreen(storage: AppStorage) {
    val context = LocalContext.current
    var activePass by remember { mutableStateOf<BookingPass?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Campus Resources & Mock Booking", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Mock Booking Showcase Card
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Reserve Study Pod (1-Click Simulation)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Simulate reserving a private library pod or collaborative discussion room.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val pass = storage.createBooking("Silent Study Pod 4B", "Science Library Floor 3", "2:00 PM - 4:00 PM", "Today")
                            activePass = pass
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Book Pod 4B", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val pass = storage.createBooking("Discussion Room 2A", "Student Union Hub", "4:00 PM - 6:00 PM", "Today")
                            activePass = pass
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text("Book Room 2A", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Active Booking Pass Banner (if booked)
        if (activePass != null) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎟️ Digital Reservation Pass", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PastelMint)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("RESERVED", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Text(activePass!!.facilityName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("📍 ${activePass!!.building} • ${activePass!!.timeSlot}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                    // Simulated QR Code Matrix Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("■ □ ■ ${activePass!!.qrCodeSeed} ■ □ ■", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Text("Curated Campus Portals", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        // Resources List opening Chrome Custom Tabs
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(storage.resources) { res ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            Text(res.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(res.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Category: ${res.category}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Button(
                            onClick = {
                                val intent = CustomTabsIntent.Builder().build()
                                intent.launchUrl(context, Uri.parse(res.url))
                            },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Open", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
