package io.github.donald_okara.components.frames

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import io.github.donald_okara.components.frames.snake_frame.SnakeFrame
import io.github.donald_okara.components.values.Values

/**
 * A builder object used to configure and instantiate [SkiFrame] components.
 *
 * This builder follows a fluent API pattern, allowing for the customization of
 * visual properties such as corner radius (curve) and opacity, as well as the
 * selection of specific frame factory implementations.
 *
 * Example usage:
 * ```kotlin
 * val frame = FrameBuilder()
 *     .setCurve(Values.Curve.Medium)
 *     .setOpacity(Values.Opacity.High)
 *     .setFrame(SkiFrameFactory.Snake)
 *     .build()
 * ```
 */
class FrameBuilder {
    private var curve: Dp? = null
    private var opacity: Float? = null

    private var frame: SkiFrameFactory? = null

    /**
     * Sets the corner radius (curvature) for the frame.
     *
     * @param curve The radius of the corners in [Dp].
     * @return The [FrameBuilder] instance for chaining.
     */
    fun setCurve(curve: Dp) = apply { this.curve = curve }

    /**
     * Sets the opacity level for the frame.
     *
     * @param opacity The opacity value, typically ranging from 0.0 (completely transparent)
     * to 1.0 (completely opaque).
     * @return The [FrameBuilder] instance for chaining.
     */
    fun setOpacity(opacity: Float) = apply { this.opacity = opacity }

    /**
     * Sets the frame factory implementation to be used when building the frame.
     *
     * @param selector The lambda to select a frame factory from the [defaultSkiFrames].
     * @return The [FrameBuilder] instance for chaining.
     */
    fun setFrame(selector: @Composable SkiFrames.() -> SkiFrameFactory) = apply {
        this.frame = SkiFrameFactory { dp, f ->
            selector(defaultSkiFrames()).create(dp, f)
        }
    }

    /**
     * Constructs and returns a [SkiFrame] based on the current configuration.
     *
     * If no specific frame factory has been set via [setFrame], it defaults to the
     * [SnakeFrame] implementation. The frame is initialized using the provided [curve]
     * and [opacity] values, or their respective defaults from [Values].
     *
     * @return A configured [SkiFrame] instance.
     */
    @Composable
    fun build(): SkiFrame {
        return when(frame) {
            null -> defaultSkiFrames()
                .snake
                .create(curve ?: Values.cornerRadius, opacity ?: Values.FRAME_OPACITY)
            else -> frame!!
                .create(curve ?: Values.cornerRadius, opacity ?: Values.FRAME_OPACITY)
        }
    }
}