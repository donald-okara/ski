package io.github.donald_okara.components.frames

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun defaultSkiFrames(): SkiFrames {
    val colors = MaterialTheme.colorScheme

    return SkiFrames(
        snake = {
            rememberFrame(colors.primary) {
                SnakeFrame(
                    leftToRight = true,
                )
            }
        }
    )
}
