package ke.don.ski.domain

import androidx.compose.runtime.Composable
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.domain.ScreenTransition

data class SlideConfig(
    val label: String,
    val notes: List<String> = emptyList(),
    val frame: @Composable () -> SkiFrame = {
        defaultSkiFrames()
            .snake
            .create(Values.cornerRadius, 0.5f)
    },
    val transition: ScreenTransition = ScreenTransition.Horizontal,
    val showHeader: Boolean = true,
    val content: @Composable () -> Unit
)