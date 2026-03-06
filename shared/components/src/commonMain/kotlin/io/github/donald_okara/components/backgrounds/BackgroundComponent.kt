package io.github.donald_okara.components.backgrounds

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.backgrounds.decorator_image.DecoratorImage
import io.github.donald_okara.components.backgrounds.pattern.AnimatedDiagonalWavyCanvas
import io.github.donald_okara.components.backgrounds.pattern.Pattern
import io.github.donald_okara.components.backgrounds.pattern.PatternComponent
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

class BackgroundComponent(
    override val pattern: Pattern,
    override val decoratorImage: DecoratorImage?,
    override val alignment: Alignment = Alignment.BottomEnd
): Background {

    @Composable
    override fun Render() {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            when(pattern){
                is Pattern.AnimatedDiagonalWavyBackground -> AnimatedDiagonalWavyCanvas(pattern, modifier = Modifier.fillMaxSize())
                else -> PatternComponent(pattern = pattern, modifier = Modifier.fillMaxSize())
            }

            decoratorImage?.let {
                Image(
                    painter = painterResource(it.image),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(decoratorImage.size)
                        .align(alignment)
                )
            }
        }
    }

}