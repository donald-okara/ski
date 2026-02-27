package io.github.donald_okara.components.frames

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.donald_okara.components.frames.basic_frame.BasicFrame
import io.github.donald_okara.components.frames.snake_frame.SnakeFrame

@Composable
fun defaultSkiFrames(): SkiFrames {
    val colors = MaterialTheme.colorScheme

    return SkiFrames(
        snake = { curve ->
            rememberFrame(colors.primary, curve) {
                SnakeFrame(
                    leftToRight = true,
                    curve = curve,
                )
            }
        },
        basic = { curve ->
            rememberFrame(colors.primary, curve) {
                BasicFrame(
                    curve = curve,
                )
            }
        }
    )
}
