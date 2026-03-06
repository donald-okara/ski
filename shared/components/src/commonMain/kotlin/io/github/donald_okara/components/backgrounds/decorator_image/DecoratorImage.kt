package io.github.donald_okara.components.backgrounds.decorator_image

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource

data class DecoratorImage(
    val image : DrawableResource,
    val size: Dp = 200.dp
)