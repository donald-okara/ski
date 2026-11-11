package io.github.donald_okara.components.backgrounds.pattern

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed interface Pattern {

    data class Wavy(
        val colors: List<Color>,
        val waveHeight: Dp = 60.dp,
        val waveLength: Dp = 300.dp,
        val waveCount: Int = 3,
        val strokeWidth: Dp = 3.dp
    ) : Pattern

    data class DiagonalWavy(
        val colors: List<Color>,
        val waveHeight: Dp = 60.dp,
        val waveLength: Dp = 300.dp,
        val waveCount: Int = 3,
        val strokeWidth: Dp = 3.dp,
        val diagonalSlope: Float = 0.5f,
    ): Pattern

    data class AnimatedDiagonalWavyBackground(
        val colors: List<Color>,
        val waveHeight: Dp = 60.dp,
        val waveLength: Dp = 300.dp,
        val waveCount: Int = 3,
        val diagonalSlope: Float = 0.5f,
        val animationDuration: Int = 1000,
        val strokeWidth: Dp = 3.dp
    ): Pattern

    /**
     * A checkered floor in perspective, scrolling toward a horizon.
     *
     * @param color Fill of the lit tiles.
     * @param alternateColor Fill of the other tiles. Transparent by default, so the backdrop shows through.
     * @param horizon Height of the horizon as a fraction of the canvas, from the top.
     * @param columnsAcross How many tiles span the bottom edge.
     * @param tileDepth Depth of one row of tiles. Smaller values pack more rows toward the horizon.
     * @param rows How many rows are drawn toward the horizon.
     * @param tileDurationMillis How long one row takes to scroll past.
     */
    data class PerspectiveFloor(
        val color: Color,
        val alternateColor: Color = Color.Transparent,
        val horizon: Float = 0.4f,
        val columnsAcross: Float = 6f,
        val tileDepth: Float = 0.5f,
        val rows: Int = 30,
        val tileDurationMillis: Int = 4000,
    ) : Pattern {
        init {
            // The floor loops over two tiles, so the duration is doubled; keep it positive and overflow-free.
            require(tileDurationMillis in 1..(Int.MAX_VALUE / 2)) {
                "tileDurationMillis must be between 1 and ${Int.MAX_VALUE / 2}"
            }
        }
    }
}
