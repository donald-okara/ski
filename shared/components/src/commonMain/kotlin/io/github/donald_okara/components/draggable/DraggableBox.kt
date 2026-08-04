package io.github.donald_okara.components.draggable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A box that can be dragged to move and resized using handles on all corners.
 *
 * @param modifier Modifier for the container.
 * @param initialWidth Initial width of the box.
 * @param initialHeight Initial height of the box.
 * @param minWidth Minimum allowed width.
 * @param minHeight Minimum allowed height.
 * @param handleColor Color of the resize handles.
 * @param content The content to be displayed inside the box.
 */
@Composable
fun DraggableBox(
    modifier: Modifier = Modifier,
    initialWidth: Dp = 200.dp,
    initialHeight: Dp = 200.dp,
    minWidth: Dp = 100.dp,
    minHeight: Dp = 100.dp,
    handleColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    var width by remember { mutableStateOf(initialWidth) }
    var height by remember { mutableStateOf(initialHeight) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(width, height)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }

        // Resize Handles on all corners
        
        // Top-Left
        ResizeHandle(
            modifier = Modifier.align(Alignment.TopStart),
            handleColor = handleColor
        ) { deltaX, deltaY ->
            val deltaW = with(density) { deltaX.toDp() }
            val deltaH = with(density) { deltaY.toDp() }
            
            val newWidth = (width - deltaW).coerceAtLeast(minWidth)
            val newHeight = (height - deltaH).coerceAtLeast(minHeight)
            
            offsetX += with(density) { (width - newWidth).toPx() }
            offsetY += with(density) { (height - newHeight).toPx() }
            
            width = newWidth
            height = newHeight
        }

        // Top-Right
        ResizeHandle(
            modifier = Modifier.align(Alignment.TopEnd),
            handleColor = handleColor
        ) { deltaX, deltaY ->
            val deltaW = with(density) { deltaX.toDp() }
            val deltaH = with(density) { deltaY.toDp() }
            
            val newHeight = (height - deltaH).coerceAtLeast(minHeight)
            offsetY += with(density) { (height - newHeight).toPx() }
            
            width = (width + deltaW).coerceAtLeast(minWidth)
            height = newHeight
        }

        // Bottom-Left
        ResizeHandle(
            modifier = Modifier.align(Alignment.BottomStart),
            handleColor = handleColor
        ) { deltaX, deltaY ->
            val deltaW = with(density) { deltaX.toDp() }
            val deltaH = with(density) { deltaY.toDp() }
            
            val newWidth = (width - deltaW).coerceAtLeast(minWidth)
            offsetX += with(density) { (width - newWidth).toPx() }
            
            width = newWidth
            height = (height + deltaH).coerceAtLeast(minHeight)
        }

        // Bottom-Right
        ResizeHandle(
            modifier = Modifier.align(Alignment.BottomEnd),
            handleColor = handleColor
        ) { deltaX, deltaY ->
            val deltaW = with(density) { deltaX.toDp() }
            val deltaH = with(density) { deltaY.toDp() }
            
            width = (width + deltaW).coerceAtLeast(minWidth)
            height = (height + deltaH).coerceAtLeast(minHeight)
        }
    }
}

@Composable
private fun ResizeHandle(
    modifier: Modifier = Modifier,
    handleColor: Color,
    onResize: (Float, Float) -> Unit
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onResize(dragAmount.x, dragAmount.y)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(handleColor, CircleShape)
                .border(1.dp, Color.White, CircleShape)
        )
    }
}