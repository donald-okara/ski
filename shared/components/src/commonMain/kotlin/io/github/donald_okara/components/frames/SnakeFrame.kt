package io.github.donald_okara.components.frames

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.values.Values

class SnakeFrame(
    private val leftToRight: Boolean = true,
) : SkiFrame {

    @Composable
    override fun Render(
        modifier: Modifier,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable BoxScope.() -> Unit
    ) {
        AnimatedSnakeFramedCard(
            leftToRight = leftToRight,
            modifier = modifier,
            header = header,
            footer = footer,
            content = content,
        )
    }
}

@Composable
fun AnimatedSnakeFramedCard(
    modifier: Modifier = Modifier,
    leftToRight: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val strokeColor = MaterialTheme.colorScheme.onSurface
    val strokeWidth = Values.lineThickness
    val cornerRadius = Values.cornerRadius
    val extraPadding = 8.dp
    val duration = 500

    LookaheadScope {
        BoxWithConstraints(modifier) {
            val density = LocalDensity.current
            val r = with(density) { cornerRadius.toPx() }
            val strokePx = with(density) { strokeWidth.toPx() }
            val extraPx = with(density) { extraPadding.toPx() }

            var headerWidth by remember { mutableStateOf(0f) }
            var headerHeight by remember { mutableStateOf(0f) }
            var footerWidth by remember { mutableStateOf(0f) }
            var footerHeight by remember { mutableStateOf(0f) }

            // Animate the header and footer dimensions
            val animatedHeaderWidth by animateFloatAsState(
                targetValue = if (header != null) headerWidth else 0f,
                animationSpec = tween(durationMillis = duration)
            )
            val animatedHeaderHeight by animateFloatAsState(
                targetValue = if (header != null) headerHeight else 0f,
                animationSpec = tween(durationMillis = duration)
            )
            val animatedFooterWidth by animateFloatAsState(
                targetValue = if (footer != null) footerWidth else 0f,
                animationSpec = tween(durationMillis = duration)
            )
            val animatedFooterHeight by animateFloatAsState(
                targetValue = if (footer != null) footerHeight else 0f,
                animationSpec = tween(durationMillis = duration)
            )

            Canvas(Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                val path = if (leftToRight)
                    headerFooterSnakePath(
                        w, h, r,
                        animatedHeaderWidth, animatedHeaderHeight,
                        animatedFooterWidth, animatedFooterHeight
                    ) else headerFooterSnakePathMirrored(
                    w, h, r,
                    animatedHeaderWidth, animatedHeaderHeight,
                    animatedFooterWidth, animatedFooterHeight
                )

                drawPath(
                    path = path,
                    color = strokeColor,
                    style = Stroke(strokePx)
                )
            }

            // HEADER
            header?.let { header ->
                Box(
                    modifier = Modifier
                        .align( if (leftToRight) Alignment.TopStart else Alignment.TopEnd)
                        .onGloballyPositioned {
                            headerWidth = it.size.width.toFloat() + extraPx
                            headerHeight = it.size.height.toFloat() + extraPx
                        }
                ) { header() }
            }

            // FOOTER
            footer?.let { footer ->
                Box(
                    modifier = Modifier
                        .align(if (leftToRight) Alignment.BottomEnd else Alignment.BottomStart)
                        .onGloballyPositioned {
                            footerWidth = it.size.width.toFloat() + extraPx
                            footerHeight = it.size.height.toFloat() + extraPx
                        }
                ) { footer() }
            }
            val safeHeaderPaddingPx = animatedHeaderHeight.coerceAtLeast(0f)
            val safeFooterPaddingPx = animatedFooterHeight.coerceAtLeast(0f)

            // CONTENT
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = with(density) { safeHeaderPaddingPx.toDp() },
                        bottom = with(density) { safeFooterPaddingPx.toDp() },
                        start = 8.dp,
                        end = 8.dp
                    )
            ) {
                content()
            }
        }
    }
}

fun headerFooterSnakePath(
    w: Float,
    h: Float,
    r: Float,
    hw: Float,
    hh: Float,
    fw: Float,
    fh: Float
) = Path().apply {
    // When hw/hh are 0, this starts at top-left corner
    // When they have values, it starts after the header cutout
    val startX = if (hh > 0f) r else r
    val startY = if (hh > 0f) hh else 0f

    moveTo(startX, startY)

    // LEFT EDGE
    if (hh > 0f) {
        quadraticTo(0f, hh, 0f, hh + r)
    } else {
        // When no header, curve at top-left corner
        quadraticTo(0f, 0f, 0f, r)
    }
    lineTo(0f, h - r)
    quadraticTo(0f, h, r, h)

    // BOTTOM EDGE
    val bottomRightX = if (fw > 0f) w - fw - r else w - r
    lineTo(bottomRightX, h)

    if (fw > 0f) {
        // FOOTER SNAKE
        quadraticTo(w - fw, h, w - fw, h - r)
        lineTo(w - fw, h - fh + r)
        quadraticTo(w - fw, h - fh, w - fw + r, h - fh)
        lineTo(w - r, h - fh)
        quadraticTo(w, h - fh, w, h - fh - r)
    } else {
        quadraticTo(w, h, w, h - r)
    }

    // RIGHT EDGE
    lineTo(w, r)
    quadraticTo(w, 0f, w - r, 0f)

    // TOP EDGE
    val topLeftX = if (hw > 0f) hw + r else r
    lineTo(topLeftX, 0f)

    if (hw > 0f) {
        // HEADER SNAKE
        quadraticTo(hw, 0f, hw, r)
        lineTo(hw, hh - r)
        quadraticTo(hw, hh, hw - r, hh)
        lineTo(startX, startY)
    }

    close()
}

fun headerFooterSnakePathMirrored(
    w: Float,
    h: Float,
    r: Float,
    hw: Float,
    hh: Float,
    fw: Float,
    fh: Float
) = Path().apply {
    // When hw/hh are 0, start at top-right corner
    // When they have values, start after the header cutout
    val startX = if (hh > 0f) w - r else w - r
    val startY = if (hh > 0f) hh else 0f

    moveTo(startX, startY)

    // RIGHT EDGE
    if (hh > 0f) {
        quadraticTo(w, hh, w, hh + r)
    } else {
        // When no header, curve at top-right corner
        quadraticTo(w, 0f, w - r, 0f)
    }
    lineTo(w, h - r)
    quadraticTo(w, h, w - r, h)

    // BOTTOM EDGE
    val bottomLeftX = if (fw > 0f) fw + r else r
    lineTo(bottomLeftX, h)

    if (fw > 0f) {
        // FOOTER SNAKE (bottom-left)
        quadraticTo(fw, h, fw, h - r)
        lineTo(fw, h - fh + r)
        quadraticTo(fw, h - fh, fw - r, h - fh)
        lineTo(r, h - fh)
        quadraticTo(0f, h - fh, 0f, h - fh - r)
    } else {
        quadraticTo(0f, h, 0f, h - r)
    }

    // LEFT EDGE
    lineTo(0f, r)
    quadraticTo(0f, 0f, r, 0f)

    // TOP EDGE
    val topRightX = if (hw > 0f) w - hw - r else w - r
    lineTo(topRightX, 0f)

    if (hw > 0f) {
        // HEADER SNAKE (top-right)
        quadraticTo(w - hw, 0f, w - hw, r)
        lineTo(w - hw, hh - r)
        quadraticTo(w - hw, hh, w - hw + r, hh)
        lineTo(startX, startY)
    }

    close()
}