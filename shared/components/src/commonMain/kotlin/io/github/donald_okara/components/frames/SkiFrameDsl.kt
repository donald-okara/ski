package io.github.donald_okara.components.frames

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
interface SkiFrame {
    @Composable
    fun Render(
        modifier: Modifier = Modifier,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable BoxScope.() -> Unit
    )
}

@Composable
fun <T : SkiFrame> rememberFrame(
    vararg keys: Any?,
    factory: () -> T
): T = remember(*keys) { factory() }


fun interface SkiFrameFactory {
    @Composable
    fun create(curve: Dp, opacity: Float): SkiFrame
}

@Stable
class SkiFrames(
    val snake: SkiFrameFactory,
    val basic: SkiFrameFactory,

    // future frames go here
)