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
}