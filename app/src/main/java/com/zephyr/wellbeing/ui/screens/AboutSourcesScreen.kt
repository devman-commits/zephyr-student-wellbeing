package com.zephyr.wellbeing.ui.screens

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.ui.theme.LavenderLight

@Composable
fun AboutSourcesScreen() {
    val context = LocalContext.current

    val sources = listOf(
        Triple("Jetpack Compose Material 3", "Official Google design guide for Material You tonal colors & components", "https://developer.android.com/jetpack/compose/designsystems/material3"),
        Triple("Android Keystore & Crypto", "Hardware-backed key generation and AES-256 zero-knowledge encryption", "https://developer.android.com/topic/security/data"),
        Triple("Android Chrome Custom Tabs", "High-performance in-app browser integration for campus resource links", "https://developer.android.com/guide/webapps/custom-tabs"),
        Triple("Android Architecture Guides", "Clean MVVM declarative state flow for modern student productivity apps", "https://developer.android.com/topic/architecture")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("About & Verified Documentation", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LavenderLight.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Zephyr Student Platform", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Built with Native Android (Kotlin + Jetpack Compose) for the Zephyr Hackathon. Designed to eliminate student burnout through peer accountability, encrypted study spaces, and gamified focus rewards.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text("Verified Pre-Existing Sources", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        sources.forEach { (title, desc, url) ->
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
                        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(url, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Button(
                        onClick = {
                            val intent = CustomTabsIntent.Builder().build()
                            intent.launchUrl(context, Uri.parse(url))
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Docs", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
