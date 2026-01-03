package ke.don.components.frames

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

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
    fun create(): SkiFrame
}

@Stable
class SkiFrames(
    val  snake: SkiFrameFactory,
    // future frames go here
)