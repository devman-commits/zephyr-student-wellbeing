package com.zephyr.wellbeing.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.wellbeing.ui.theme.GoldCoin
import com.zephyr.wellbeing.ui.theme.LavenderLight
import com.zephyr.wellbeing.ui.theme.PrimaryLavender

@Composable
fun ProgressDonutArc(
    score: Int = 200,
    maxScore: Int = 300,
    modifier: Modifier = Modifier
) {
    val targetPercent = (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f)
    val animatedPercent by animateFloatAsState(
        targetValue = targetPercent,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "semicircleProgress"
    )

    Box(
        modifier = modifier
            .width(200.dp)
            .height(115.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val radius = (size.width - strokeWidth) / 2
            val diameter = radius * 2
            val arcSize = Size(diameter, diameter)
            // Center the semicircle so the base line aligns perfectly
            val topLeft = Offset(strokeWidth / 2, size.height - radius - (strokeWidth / 2))

            // Background Track Arc (True 180° Semicircle from left to right)
            drawArc(
                color = LavenderLight.copy(alpha = 0.35f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Dynamic Active Arc (Animated)
            val sweep = 180f * animatedPercent
            if (sweep > 0f) {
                drawArc(
                    color = PrimaryLavender,
                    startAngle = 180f,
                    sweepAngle = sweep * 0.78f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Golden Accent Lead
                drawArc(
                    color = GoldCoin,
                    startAngle = 180f + (sweep * 0.78f),
                    sweepAngle = sweep * 0.22f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        ) {
            Text(
                text = score.toString(),
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Score",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
