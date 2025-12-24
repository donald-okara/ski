package com.example.components.frames

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFrame(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon = frameShapes.random(),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    rotate: Boolean = true,
    durationMillis: Int = 30000,
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
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
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
        ) {
            content?.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFrameClipped(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon = frameShapes.random(),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    sizeDp: Int,
    content: (@Composable () -> Unit)? = null,
) {
    val shape = polygon.toShape()

    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(500)
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                clip = false
            }
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp.dp)
                .clip(shape)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0.8f),
                            backgroundColor.copy(alpha = 0.6f),
                            backgroundColor.copy(alpha = 0.4f),
                            backgroundColor.copy(alpha = 0.2f)
                        )
                    )
                )
        )

        content?.let { contentLambda ->
            // Top half - unclipped overflow
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .drawWithContent {
                        clipRect(
                            left = 0f,
                            top = 0f,
                            right = size.width,
                            bottom = size.height / 2
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                contentLambda()
            }

            // Bottom half - clipped to rotating shape
            Box(
                modifier = Modifier
                    .clip(shape)
                    .drawWithContent {
                        clipRect(
                            left = 0f,
                            top = size.height / 2,
                            right = size.width,
                            bottom = size.height
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                contentLambda()
            }
        }
    }
}

@Composable
fun ExpressivePictureFrame(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon = frameShapes.random(),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    sizeDp: Int = 200,
    image: DrawableResource,
) {
    ExpressiveFrameClipped(
        modifier = modifier,
        polygon = polygon,
        backgroundColor = backgroundColor,
        sizeDp = sizeDp
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size((sizeDp).dp)
        )
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
