package ke.don.ski.presentation.ui.background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WavyBackground(
    modifier: Modifier = Modifier,
    waveHeight: Dp = 60.dp,
    waveLength: Dp = 300.dp,
    waveCount: Int = 3,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    )
) {
    require(waveLength > 0.dp) { "waveLength must be > 0.dp" }
    require(waveCount >= 0) { "waveCount must be >= 0" }

    require(waveCount == 0 || colors.isNotEmpty()) {
        "colors must not be empty when waveCount > 0"
    }

    val density = LocalDensity.current

    Canvas(modifier = modifier.fillMaxSize()) {
        val waveHeightPx = with(density) { waveHeight.toPx() }
        val waveLengthPx = with(density) { waveLength.toPx() }
        val centerY = size.height / 2

        for (i in 0 until waveCount) {
            val path = Path()
            val yOffset = centerY + (i - waveCount / 2f) * waveHeightPx * 1.5f
            val color = colors[i % colors.size]

            path.moveTo(0f, yOffset)

            var x = 0f
            while (x < size.width + waveLengthPx) {
                path.quadraticTo(
                    x + waveLengthPx / 4,
                    yOffset - waveHeightPx,
                    x + waveLengthPx / 2,
                    yOffset
                )
                path.quadraticTo(
                    x + 3 * waveLengthPx / 4,
                    yOffset + waveHeightPx,
                    x + waveLengthPx,
                    yOffset
                )
                x += waveLengthPx
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

@Composable
fun DiagonalWavyBackground(
    modifier: Modifier = Modifier,
    waveHeight: Dp = 60.dp,
    waveLength: Dp = 300.dp,
    waveCount: Int = 3,
    diagonalSlope: Float = 0.5f, // how steep the diagonal is
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    )
) {
    require(waveLength > 0.dp) { "waveLength must be > 0.dp" }
    require(waveCount >= 0) { "waveCount must be >= 0" }

    require(waveCount == 0 || colors.isNotEmpty()) {
        "colors must not be empty when waveCount > 0"
    }

    val density = LocalDensity.current

    Canvas(modifier = modifier.fillMaxSize()) {
        val waveHeightPx = with(density) { waveHeight.toPx() }
        val waveLengthPx = with(density) { waveLength.toPx() }
        val centerY = size.height / 2

        for (i in 0 until waveCount) {
            val path = Path()
            val color = colors[i % colors.size]

            val yOffsetBase = centerY + (i - waveCount / 2f) * waveHeightPx * 1.5f

            path.moveTo(0f, yOffsetBase)

            var x = 0f
            while (x < size.width + waveLengthPx) {
                val yOffset = yOffsetBase + x * diagonalSlope

                path.quadraticTo(
                    x + waveLengthPx / 4,
                    yOffset - waveHeightPx,
                    x + waveLengthPx / 2,
                    yOffset
                )
                path.quadraticTo(
                    x + 3 * waveLengthPx / 4,
                    yOffset + waveHeightPx,
                    x + waveLengthPx,
                    yOffset
                )
                x += waveLengthPx
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

@Composable
fun AnimatedDiagonalWavyBackground(
    modifier: Modifier = Modifier,
    waveHeight: Dp = 60.dp,
    waveLength: Dp = 300.dp,
    waveCount: Int = 3,
    diagonalSlope: Float = 0.5f, // vertical offset per px
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    ),
    animationDuration: Int = 8000 // milliseconds
) {
    require(waveLength > 0.dp) { "waveLength must be > 0.dp" }
    require(waveCount >= 0) { "waveCount must be >= 0" }

    require(waveCount == 0 || colors.isNotEmpty()) {
        "colors must not be empty when waveCount > 0"
    }
    
    val density = LocalDensity.current

    // Infinite horizontal offset animation
    val infiniteTransition = rememberInfiniteTransition()
    val offsets = List(waveCount) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = with(density) { waveLength.toPx() },
            animationSpec = infiniteRepeatable(
                animation = tween(animationDuration + i * 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val waveHeightPx = with(density) { waveHeight.toPx() }
        val waveLengthPx = with(density) { waveLength.toPx() }
        val centerY = size.height / 2

        for (i in 0 until waveCount) {
            val path = Path()
            val color = colors[i % colors.size]

            val yOffsetBase = centerY + (i - waveCount / 2f) * waveHeightPx * 1.5f
            val horizontalOffset = offsets[i].value

            path.moveTo(-horizontalOffset, yOffsetBase)

            var x = -horizontalOffset
            while (x < size.width + waveLengthPx) {
                val yOffset = yOffsetBase + x * diagonalSlope

                path.quadraticTo(
                    x + waveLengthPx / 4,
                    yOffset - waveHeightPx,
                    x + waveLengthPx / 2,
                    yOffset
                )
                path.quadraticTo(
                    x + 3 * waveLengthPx / 4,
                    yOffset + waveHeightPx,
                    x + waveLengthPx,
                    yOffset
                )
                x += waveLengthPx
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}