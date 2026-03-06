package io.github.donald_okara.components.backgrounds.pattern

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// Now a pure DrawScope function
fun DrawScope.drawAnimatedDiagonalWavy(
    pattern: Pattern.AnimatedDiagonalWavyBackground,
    offsets: List<Float>
) {
    require(pattern.waveLength > 0.dp) { "waveLength must be > 0.dp" }
    require(pattern.waveCount >= 0) { "waveCount must be >= 0" }
    require(pattern.waveCount == 0 || pattern.colors.isNotEmpty()) {
        "colors must not be empty when waveCount > 0"
    }
    val waveHeightPx = pattern.waveHeight.toPx()
    val waveLengthPx = pattern.waveLength.toPx()
    val strokeWidthPx = pattern.strokeWidth.toPx()
    val centerY = size.height / 2f
    val path = Path()

    for (i in 0 until pattern.waveCount) {
        path.reset()
        val color = pattern.colors[i % pattern.colors.size]
        val yOffsetBase = centerY + (i - pattern.waveCount / 2f) * waveHeightPx * 1.5f
        val horizontalOffset = offsets.getOrElse(i) { 0f }

        path.moveTo(-horizontalOffset, yOffsetBase)
        var x = -horizontalOffset

        while (x < size.width + waveLengthPx) {
            val yOffset = yOffsetBase + x * pattern.diagonalSlope
            path.quadraticTo(x + waveLengthPx / 4, yOffset - waveHeightPx, x + waveLengthPx / 2, yOffset)
            path.quadraticTo(x + 3 * waveLengthPx / 4, yOffset + waveHeightPx, x + waveLengthPx, yOffset)
            x += waveLengthPx
        }

        drawPath(path = path, color = color, style = Stroke(width = strokeWidthPx))
    }
}