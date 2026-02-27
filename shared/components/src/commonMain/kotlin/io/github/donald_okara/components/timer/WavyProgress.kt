package io.github.donald_okara.components.timer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.values.Values
import kotlin.math.PI
import kotlin.math.sin


@Composable
fun WavyVerticalProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Values.cornerRadius,
    colors: List<Color> = listOf(Color(0xFFFF6E40), MaterialTheme.colorScheme.primary),
    waveAmplitude: Dp = 8.dp,
    waveFrequency: Float = 2f,
    waveSpeed: Int = 1000,
    waveCount: Int = 2 // number of overlapping waves
) {
    val infiniteTransition = rememberInfiniteTransition()
    val waveOffsets = List(waveCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = waveSpeed + index * 300, // staggered speed
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(modifier = modifier.clip(RoundedCornerShape(cornerRadius))) {
        val widthPx = size.width
        val heightPx = size.height
        val fillHeight = heightPx * progress
        val amplitudePx = waveAmplitude.toPx()

        repeat(waveCount) { i ->
            val path = Path().apply {
                moveTo(0f, heightPx)
                for (x in 0..widthPx.toInt()) {
                    val phase = waveOffsets[i].value * 2 * PI
                    val y = heightPx - fillHeight +
                            amplitudePx * sin((x / widthPx) * waveFrequency * 2 * PI + phase).toFloat()
                    lineTo(x.toFloat(), y)
                }
                lineTo(widthPx, heightPx)
                close()
            }

            // Slightly reduce alpha for overlapping effect
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = colors.map { it.copy(alpha = 0.5f) }, // half transparency
                    startY = heightPx - fillHeight - amplitudePx,
                    endY = heightPx
                )
            )
        }
    }
}


// Helper functions
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return when {
        hours > 0 -> "${hours}h ${minutes.pad2()}m"
        minutes > 0 -> "${minutes}m ${seconds.pad2()}s"
        else -> "${seconds}s"
    }
}

private fun Long.pad2(): String = if (this < 10) "0$this" else this.toString()