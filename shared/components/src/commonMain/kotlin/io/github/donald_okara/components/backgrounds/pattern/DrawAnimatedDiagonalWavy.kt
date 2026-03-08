package io.github.donald_okara.components.backgrounds.pattern

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// Now a pure DrawScope function
fun DrawScope.drawAnimatedDiagonalWavy(
    pattern: Pattern.AnimatedDiagonalWavyBackground,
    offset: Float
) {
    require(pattern.waveLength > 0.dp) { "waveLength must be > 0.dp" }
    require(pattern.waveCount >= 0) { "waveCount must be >= 0" }
    require(pattern.waveCount == 0 || pattern.colors.isNotEmpty()) {
        "colors must not be empty when waveCount > 0"
    }

    val waveHeightPx = pattern.waveHeight.toPx()
    val waveLengthPx = pattern.waveLength.toPx()
    val strokeWidthPx = pattern.strokeWidth.toPx()

    val normalizedOffset = offset % waveLengthPx

    val centerY = size.height / 2f
    val path = Path()

    for (i in 0 until pattern.waveCount) {

        path.reset()

        val color = pattern.colors[i % pattern.colors.size]
        val yOffsetBase =
            centerY + (i - (pattern.waveCount - 1) / 2f) * waveHeightPx * 1.5f

        var x = -waveLengthPx - normalizedOffset
        path.moveTo(x, yOffsetBase + x * pattern.diagonalSlope)

        while (x < size.width + waveLengthPx) {

            path.quadraticTo(
                x + waveLengthPx / 4,
                yOffsetBase + (x + waveLengthPx / 4) * pattern.diagonalSlope - waveHeightPx,
                x + waveLengthPx / 2,
                yOffsetBase + (x + waveLengthPx / 2) * pattern.diagonalSlope
            )

            path.quadraticTo(
                x + 3 * waveLengthPx / 4,
                yOffsetBase + (x + 3 * waveLengthPx / 4) * pattern.diagonalSlope + waveHeightPx,
                x + waveLengthPx,
                yOffsetBase + (x + waveLengthPx) * pattern.diagonalSlope
            )

            x += waveLengthPx
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidthPx)
        )
    }
}
