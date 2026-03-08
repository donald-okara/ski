package io.github.donald_okara.components.backgrounds.pattern

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun PatternComponent(
    pattern: Pattern,
    modifier: Modifier = Modifier
) {
    when (pattern) {
        is Pattern.AnimatedDiagonalWavyBackground ->
            AnimatedDiagonalWavyCanvas(pattern = pattern, modifier = modifier)

        else ->
            Canvas(modifier = modifier) {
                drawPattern(pattern)
            }
    }
}

fun DrawScope.drawPattern(pattern: Pattern) {
    when (pattern) {

        is Pattern.Wavy -> drawWavy(pattern)

        is Pattern.DiagonalWavy -> drawDiagonalWavy(pattern)

        is Pattern.AnimatedDiagonalWavyBackground -> {}
    }
}

@Composable
fun AnimatedDiagonalWavyCanvas(
    pattern: Pattern.AnimatedDiagonalWavyBackground,
    modifier: Modifier = Modifier
) {
    val offset = pattern.offset()

    Canvas(modifier = modifier) {
        drawAnimatedDiagonalWavy(pattern, offset.value)
    }
}