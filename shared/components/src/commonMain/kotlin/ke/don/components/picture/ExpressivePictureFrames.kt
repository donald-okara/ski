package ke.don.components.picture

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt
import kotlin.random.Random

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFrameClipped(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon = frameShapes.random(),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    sizeDp: Int,
    index: Int = 0,
    imageOffset: Int = -10,
    floating: Boolean = false,
    brushType: BrushType = BrushType.SWEEP,
    content: @Composable () -> Unit,
) {
    val shape = polygon.toShape()
    val density = LocalDensity.current
    val imageOffsetPx = with(density) { imageOffset.dp.toPx() }

    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(500),
        label = "FrameColor"
    )

    val brushColors = listOf(
        animatedColor,
        animatedColor.copy(alpha = 0.8f),
        animatedColor.copy(alpha = 0.6f),
        animatedColor.copy(alpha = 0.4f),
        animatedColor.copy(alpha = 0.2f)
    )

    val brush = remember(brushType, animatedColor) {
        when (brushType) {
            BrushType.LINEAR -> Brush.linearGradient(brushColors)
            BrushType.SWEEP -> Brush.sweepGradient(brushColors)
            BrushType.RADIAL -> Brush.radialGradient(brushColors)
            BrushType.VERTICAL -> Brush.verticalGradient(brushColors)
            BrushType.HORIZONTAL -> Brush.horizontalGradient(brushColors)
        }
    }

    val (offsetX, offsetY) = randomFloatingOffset(seed = index, (sizeDp / 4).toFloat())

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .then(
                if (floating)
                    Modifier.offset {
                        IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                    }
                else Modifier
            )
    ) {
        // Background frame
        Box(
            Modifier
                .clip(shape)
                .matchParentSize()
                .drawBehind {
                    drawRoundRect(
                        brush = brush,
                        size = size,
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                }
        )

        // Image (drawn ONCE)
        Box(
            modifier = Modifier
                .matchParentSize()
                .expressiveImageClip(
                    shape = shape,
                    imageOffsetPx = imageOffsetPx
                )
        ) {
            content()
        }
    }
}


@Composable
fun ExpressivePictureFrame(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon = frameShapes.random(),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    sizeDp: Int = 200,
    index: Int = 0,
    imageOffset: Int = -10,
    floating: Boolean = true,
    brushType: BrushType = BrushType.SWEEP,
    image: DrawableResource,
) {
    ExpressiveFrameClipped(
        modifier = modifier,
        polygon = polygon,
        brushType = brushType,
        index = index,
        backgroundColor = backgroundColor,
        sizeDp = sizeDp,
        imageOffset = imageOffset,
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

fun Modifier.expressiveImageClip(
    shape: Shape,
    imageOffsetPx: Float,
) = this.drawWithContent {
    val w = size.width
    val h = size.height
    val halfH = h / 2f

    // Convert shape to outline path
    val outline = shape.createOutline(size, layoutDirection, this)
    val shapePath = when (outline) {
        is Outline.Generic -> outline.path
        is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
        is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
    }

    // 1. Draw TOP half (no clipping)
    clipRect(
        left = 0f,
        top = 0f,
        right = w,
        bottom = halfH
    ) {
        translate(top = imageOffsetPx) {
            this@drawWithContent.drawContent()
        }
    }

    // 2. Draw BOTTOM half (clipped to shape)
    clipPath(shapePath) {
        clipRect(
            left = 0f,
            top = halfH,
            right = w,
            bottom = h
        ) {
            translate(top = imageOffsetPx) {
                this@drawWithContent.drawContent()
            }
        }
    }
}

@Composable
fun randomFloatingOffset(seed: Int = 0, animationDistance: Float = 50f): Pair<Float, Float> {
    val random = remember(seed) { Random(seed) }

    val rangeX =
        remember(seed) { random.nextFloat() * animationDistance - animationDistance } // -10 to +10
    val rangeY = remember(seed) { random.nextFloat() * animationDistance - animationDistance }

    val durationX = remember(seed) { random.nextInt(2000, 4000) }
    val durationY = remember(seed) { random.nextInt(2500, 5000) }

    val infiniteTransition = rememberInfiniteTransition(label = "Floating_$seed")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = -rangeX,
        targetValue = rangeX,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = durationX, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OffsetX_$seed"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -rangeY,
        targetValue = rangeY,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = durationY, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OffsetY_$seed"
    )

    return offsetX to offsetY
}


enum class BrushType {
    LINEAR, SWEEP, RADIAL, VERTICAL, HORIZONTAL

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val frameShapes = listOf(
    MaterialShapes.Bun,
    MaterialShapes.Sunny,
    MaterialShapes.Cookie4Sided,
    MaterialShapes.Cookie6Sided,
    MaterialShapes.Cookie7Sided,
    MaterialShapes.Clover4Leaf,
    MaterialShapes.Clover8Leaf
)
