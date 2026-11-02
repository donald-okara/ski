package io.github.donald_okara.components.backgrounds.pattern

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun PerspectiveFloorCanvas(
    pattern: Pattern.PerspectiveFloor,
    modifier: Modifier = Modifier
) {
    // Two tiles per loop so the checker parity repeats and the loop is seamless.
    val scroll by rememberInfiniteTransition(label = "floor").animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            tween(pattern.tileDurationMillis * 2, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "floorScroll",
    )

    Canvas(modifier = modifier) {
        drawPerspectiveFloor(pattern, scroll)
    }
}

/**
 * Draws [pattern] with the floor moved [scroll] rows toward the viewer.
 *
 * Ground depth d maps to screen y = horizon + floorHeight / d, so d = 1 is the bottom edge.
 */
fun DrawScope.drawPerspectiveFloor(
    pattern: Pattern.PerspectiveFloor,
    scroll: Float
) {
    require(pattern.columnsAcross > 0f) { "columnsAcross must be > 0" }
    require(pattern.tileDepth > 0f) { "tileDepth must be > 0" }

    val horizonY = size.height * pattern.horizon
    val floorHeight = size.height - horizonY
    val cx = size.width / 2f
    val cellWidth = size.width / pattern.columnsAcross
    val whole = floor(scroll).toInt()
    val frac = scroll - whole
    val path = Path()

    for (r in -1 until pattern.rows) {
        val id = r - whole
        val dNear = 1f + (r + frac) * pattern.tileDepth
        val dFar = 1f + (r + 1 + frac) * pattern.tileDepth
        if (dFar <= 0f) continue
        val nearSafe = dNear.coerceAtLeast(0.05f)
        val yNear = horizonY + floorHeight / nearSafe
        val yFar = horizonY + floorHeight / dFar
        if (yFar > size.height + 1f && yNear > size.height + 1f) continue
        val limit = ceil(size.width / 2f / cellWidth * dFar).toInt() + 1
        for (i in -limit until limit) {
            val color = if ((i + id) and 1 == 0) pattern.color else pattern.alternateColor
            if (color == Color.Transparent) continue
            path.reset()
            path.moveTo(cx + i * cellWidth / nearSafe, yNear)
            path.lineTo(cx + (i + 1) * cellWidth / nearSafe, yNear)
            path.lineTo(cx + (i + 1) * cellWidth / dFar, yFar)
            path.lineTo(cx + i * cellWidth / dFar, yFar)
            path.close()
            drawPath(path, color)
        }
    }
}
