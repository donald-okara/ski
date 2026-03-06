package io.github.donald_okara.components.backgrounds

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.backgrounds.decorator_image.DecoratorImage
import io.github.donald_okara.components.backgrounds.pattern.Pattern

@Stable
interface Background {
    val pattern: Pattern
    val decoratorImage: DecoratorImage?
    val alignment: Alignment

    @Composable
    fun Render()
}

@Composable
fun <T : Background> rememberBackground(
    vararg keys: Any?,
    factory: () -> T
): T = remember(*keys) { factory() }