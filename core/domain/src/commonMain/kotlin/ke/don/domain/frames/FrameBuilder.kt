package ke.don.domain.frames

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import ke.don.domain.values.Values

val LocalSkiFrames = staticCompositionLocalOf<SkiFrames> {
    error("No SkiFrames provided")
}

class FrameBuilder {
    private var curve: Dp? = null
    private var opacity: Float? = null

    private var frame: SkiFrameFactory? = null

    fun setCurve(curve: Dp) = apply { this.curve = curve }

    fun setOpacity(opacity: Float) = apply { this.opacity = opacity }

    fun setFrame(selector: @Composable SkiFrames.() -> SkiFrameFactory) = apply {
        this.frame = SkiFrameFactory { dp, f ->
            selector(LocalSkiFrames.current).create(dp, f)
        }
    }

    @Composable
    fun build(): SkiFrame {
        val frames = LocalSkiFrames.current
        return when(frame) {
            null -> frames.snake.create(curve ?: Values.cornerRadius, opacity ?: Values.FRAME_OPACITY)
            else -> frame!!.create(curve ?: Values.cornerRadius, opacity ?: Values.FRAME_OPACITY)
        }
    }
}
