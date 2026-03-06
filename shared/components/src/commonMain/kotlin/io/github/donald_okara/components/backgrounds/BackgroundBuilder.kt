package io.github.donald_okara.components.backgrounds

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import io.github.donald_okara.components.backgrounds.decorator_image.DecoratorImage
import io.github.donald_okara.components.backgrounds.pattern.Pattern
import io.github.donald_okara.components.backgrounds.pattern.PatternDefaults

object BackgroundBuilder {
    private var pattern: Pattern? = null
    private var decoratorImage: DecoratorImage? = null
    private var alignment: Alignment = Alignment.BottomEnd

    fun setPattern(pattern: Pattern) = apply { this.pattern = pattern }

    fun setDecoratorImage(decoratorImage: DecoratorImage) = apply { this.decoratorImage = decoratorImage }

    fun setAlignment(alignment: Alignment) = apply { this.alignment = alignment }

    @Composable
    fun build(): Background {
        val colors = PatternDefaults.colors
        val finalPattern = pattern ?: Pattern.DiagonalWavy(colors = colors)
        val finalDecoratorImage = decoratorImage

        return rememberBackground {
            BackgroundComponent(
                pattern = finalPattern,
                decoratorImage = finalDecoratorImage,
                alignment = alignment
            )
        }
    }
}