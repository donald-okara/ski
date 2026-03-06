package io.github.donald_okara.components.backgrounds.pattern

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

object PatternDefaults {

    val colors: List<Color>
        @Composable get() = MaterialTheme.colorScheme.primary.getColors()
}

fun Color.getColors(): List<Color> = listOf(
    this,
    this.copy(alpha = 0.5f),
    this.copy(alpha = 0.2f),
    this.copy(alpha = 0.1f),
)


@Composable
fun Pattern.AnimatedDiagonalWavyBackground.offsets(): List<State<Float>> {
    val infiniteTransition = rememberInfiniteTransition()
    val density = LocalDensity.current

    return List(waveCount) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = with(density) { waveLength.toPx() },
            animationSpec = infiniteRepeatable(
                animation = tween(animationDuration + i * 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
}

@Composable
fun Pattern.AnimatedDiagonalWavyBackground.offset(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition()
    val density = LocalDensity.current
    val waveLengthPx = with(density) { waveLength.toPx() }

    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = waveLengthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
}