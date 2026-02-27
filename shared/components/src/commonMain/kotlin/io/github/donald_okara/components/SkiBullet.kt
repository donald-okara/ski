package io.github.donald_okara.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LinearBullet(
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFFFF6E40),
    height: Int = 40
) {
    Box(
        modifier = modifier
            .width((height.coerceAtLeast(1) / 10f).dp)
            .height(height.coerceAtLeast(1).dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        accentColor
                    )
                )
            )
    )
}

@Composable
fun DotBullet(
    modifier: Modifier = Modifier,
    size: Int = 8,
    accentColor: Color = Color(0xFFFF6E40)
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor,
                        MaterialTheme.colorScheme.primary,
                    )
                )
            )
    )
}