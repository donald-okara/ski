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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFrameClipped(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon = frameShapes.random(),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    sizeDp: Int,
    floating: Boolean = false,
    brushType: BrushType = BrushType.SWEEP,
    content: (@Composable () -> Unit)? = null,
) {
    val shape = polygon.toShape()

    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(500)
    )

    val brushColors = listOf(
        animatedColor,
        animatedColor.copy(alpha = 0.8f),
        animatedColor.copy(alpha = 0.6f),
        animatedColor.copy(alpha = 0.4f),
        animatedColor.copy(alpha = 0.2f)
    )

    val brush by remember(brushType) {
        derivedStateOf {
            when (brushType) {
                BrushType.LINEAR -> Brush.linearGradient(
                    colors = brushColors
                )

                BrushType.SWEEP -> Brush.sweepGradient(
                    colors = brushColors
                )

                BrushType.RADIAL -> Brush.radialGradient(
                    colors = brushColors
                )

                BrushType.VERTICAL -> Brush.verticalGradient(
                    colors = brushColors
                )

                BrushType.HORIZONTAL -> Brush.horizontalGradient(
                    colors = brushColors
                )
            }
        }
    }

    // 1. Create an infinite transition for the floating effect
    val infiniteTransition = rememberInfiniteTransition(label = "FloatingAnimation")

    // 2. Animate the Y offset (top to bottom)
    val floatingOffsetY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Go up and down smoothly
        ),
        label = "OffsetY"
    )

    // 3. Animate the X offset (side to side)
    val floatingOffsetX by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Go left and right smoothly
        ),
        label = "OffsetX"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                clip = false
            }
            .then(
                if (floating)
                // 4. Apply both X and Y offsets
                    Modifier
                        .offset {
                            IntOffset(
                                x = floatingOffsetX.roundToInt(),
                                y = floatingOffsetY.roundToInt()
                            )
                        }
                else
                    Modifier
            ),
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp.dp)
                .clip(shape)
                .background(brush)
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
    floating: Boolean = true,
    brushType: BrushType = BrushType.SWEEP,
    image: DrawableResource,
) {
    ExpressiveFrameClipped(
        modifier = modifier,
        polygon = polygon,
        brushType = brushType,
        backgroundColor = backgroundColor,
        sizeDp = sizeDp,
        floating = floating
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size((sizeDp).dp)
        )
    }
}


enum class BrushType {
    LINEAR, SWEEP, RADIAL, VERTICAL, HORIZONTAL

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val frameShapes = listOf(
    MaterialShapes.Sunny,
    MaterialShapes.Cookie4Sided,
    MaterialShapes.Cookie6Sided,
    MaterialShapes.Cookie7Sided,
    MaterialShapes.PixelCircle
)
