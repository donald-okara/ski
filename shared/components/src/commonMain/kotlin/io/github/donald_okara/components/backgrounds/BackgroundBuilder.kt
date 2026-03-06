package io.github.donald_okara.components.backgrounds

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import io.github.donald_okara.components.backgrounds.decorator_image.DecoratorImage
import io.github.donald_okara.components.backgrounds.pattern.Pattern
import io.github.donald_okara.components.backgrounds.pattern.PatternDefaults

/**
 * A builder class for creating and configuring [Background] instances.
 *
 * This builder follows a fluent API pattern, allowing for the customization of the
 * background's pattern, decorative image, and the alignment of that image.
 *
 * Example usage:
 * ```kotlin
 * val background = BackgroundBuilder()
 *     .setPattern(Pattern.Wavy)
 *     .setDecoratorImage(DecoratorImage(image: Resources.ANDROID))
 *     .setAlignment(Alignment.Center)
 *     .build()
 * ```
 */
class BackgroundBuilder {
    private var pattern: Pattern? = null
    private var decoratorImage: DecoratorImage? = null
    private var alignment: Alignment = Alignment.BottomEnd

    /**
     * Sets the background pattern for the [Background].
     *
     * @param pattern The [Pattern] to be used in the background.
     * @return This [BackgroundBuilder] instance for method chaining.
     */
    fun setPattern(pattern: Pattern) = apply { this.pattern = pattern }

    /**
     * Sets the decorative image to be displayed over the background pattern.
     *
     * @param decoratorImage The [DecoratorImage] to be used.
     * @return This [BackgroundBuilder] instance for method chaining.
     */
    fun setDecoratorImage(decoratorImage: DecoratorImage) = apply { this.decoratorImage = decoratorImage }

    /**
     * Sets the alignment for the [DecoratorImage] within the background.
     *
     * @param alignment The [Alignment] to be used for positioning the decorative image.
     * @return The current [BackgroundBuilder] instance for method chaining.
     */
    fun setAlignment(alignment: Alignment) = apply { this.alignment = alignment }

    /**
     * Builds and returns a [Background] instance based on the current configuration.
     *
     * If no pattern is specified, it defaults to [Pattern.DiagonalWavy] using
     * the default colors from [PatternDefaults].
     *
     * @return A [Background] composable state object initialized with the
     * configured pattern, decorator image, and alignment.
     */
    @Composable
    fun build(): Background {
        val colors = PatternDefaults.colors
        val finalPattern = pattern ?: Pattern.DiagonalWavy(colors = colors)
        val finalDecoratorImage = decoratorImage

        return rememberBackground(
            finalPattern, finalDecoratorImage, alignment
        ) {
            BackgroundComponent(
                pattern = finalPattern,
                decoratorImage = finalDecoratorImage,
                alignment = alignment
            )
        }
    }
}