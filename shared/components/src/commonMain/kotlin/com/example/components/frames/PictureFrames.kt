package com.example.components.frames

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressivePictureFrame(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon = frameShapes.random(),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    rotate: Boolean = true,
    content: (@Composable () -> Unit)? = null,
) {
    val shape = polygon.toShape()

    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(500)
    )
    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteRotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationAnimation"
    )

    Box(
        modifier = modifier
            .then(
                if (rotate)
                    Modifier
                        .graphicsLayer {
                            rotationZ = rotation

                        } else Modifier
            )
            .clip(shape)
            .background(color = animatedColor)
            .wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (rotate)
                        Modifier
                            .graphicsLayer {
                                rotationZ = -rotation
                            }
                    else Modifier
                )
                .padding(16.dp)
        ) {
            content?.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val frameShapes = listOf(
    MaterialShapes.Sunny,
    MaterialShapes.Cookie4Sided,
    MaterialShapes.Cookie6Sided,
    MaterialShapes.Cookie7Sided,
    MaterialShapes.PixelCircle
)
