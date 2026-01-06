package io.github.donald_okara.components.picture

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Renders a square composable that displays content framed by a polygonal outline which can rotate.
 *
 * The outline is drawn outside the content bounds; the content area is inset by [outlinePadding] and
 * [contentPadding]. [strokeWidth] controls the outline thickness and [rotationDurationMs] controls
 * the speed of a full rotation when [rotate] is true.
 *
 * @param modifier Modifier applied to the outer container.
 * @param polygon The polygon used to produce the outline shape.
 * @param color Color of the outline.
 * @param rotate If true, the outline rotates continuously.
 * @param rotationDurationMs Duration in milliseconds for one full rotation.
 * @param contentPadding Padding between the outline and the content.
 * @param outlinePadding Extra space reserved between the composable bounds and the content for the outline.
 * @param strokeWidth Width of the outline stroke.
 * @param content Composable content placed inside the framed area.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFrame(
    modifier: Modifier = Modifier,
    polygon: RoundedPolygon,
    color: Color = MaterialTheme.colorScheme.primary,
    rotate: Boolean = true,
    rotationDurationMs: Int = 4000,
    contentPadding: Dp = 8.dp,
    outlinePadding: Dp = 6.dp,
    strokeWidth: Dp = 2.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = polygon.toShape()

    val rotation = if (rotate) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val anim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(rotationDurationMs, easing = LinearEasing)
            ),
            label = "rotationAnim"
        )
        anim
    } else 0f

    Box(
        modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center
    ) {

        // Rotating outline — OUTSIDE content bounds
        Canvas(
            modifier = Modifier.matchParentSize().aspectRatio(1f).then(
                if (rotate) {
                    Modifier.graphicsLayer { rotationZ = rotation }
                } else Modifier)) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2

            inset(inset) {
                drawOutline(
                    outline = shape.createOutline(
                        size = Size(
                            width = size.width - strokePx, height = size.height - strokePx
                        ), layoutDirection = layoutDirection, density = this
                    ), style = Stroke(width = strokePx), color = color
                )
            }
        }

        // Content box defines minimum size
        Box(
            modifier = Modifier.padding(outlinePadding).padding(contentPadding),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}


/**
 * Renders a framed, clipped image area with a gradient background and optional floating animation.
 *
 * The frame is clipped to the provided polygon shape, the background color is animated toward
 * `backgroundColor` and used to build a gradient (`brushType`), and the content is clipped using
 * a split-image effect where the top half remains intact and the bottom half is clipped to the shape.
 *
 * @param polygon The rounded polygon shape used to clip the frame and image.
 * @param backgroundColor Target color for the frame background; used to build the gradient brush.
 * @param sizeDp Size of the frame in density-independent pixels (both width and height).
 * @param index Seed used to derive a deterministic floating offset when `floating` is true.
 * @param imageOffset Vertical offset in Dp applied to the image content (positive moves content down).
 * @param floating When true, applies a subtle animated floating translation to the entire frame.
 * @param brushType The gradient type used for the frame background (linear, sweep, radial, etc.).
 * @param content Composable that provides the image or other content placed inside the clipped frame.
 */
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
        targetValue = backgroundColor, animationSpec = tween(500), label = "FrameColor"
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
        modifier = modifier.size(sizeDp.dp).then(if (floating) Modifier.offset {
            IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
        }
        else Modifier)) {
        // Background frame
        Box(
            Modifier.clip(shape).matchParentSize().drawBehind {
                drawRoundRect(
                    brush = brush, size = size, cornerRadius = CornerRadius(16.dp.toPx())
                )
            })

        // Image (drawn ONCE)
        Box(
            modifier = Modifier.matchParentSize().expressiveImageClip(
                shape = shape, imageOffsetPx = imageOffsetPx
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
        left = 0f, top = 0f, right = w, bottom = halfH
    ) {
        translate(top = imageOffsetPx) {
            this@drawWithContent.drawContent()
        }
    }

    // 2. Draw BOTTOM half (clipped to shape)
    clipPath(shapePath) {
        clipRect(
            left = 0f, top = halfH, right = w, bottom = h
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
        initialValue = -rangeX, targetValue = rangeX, animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = durationX, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "OffsetX_$seed"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -rangeY, targetValue = rangeY, animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = durationY, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "OffsetY_$seed"
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