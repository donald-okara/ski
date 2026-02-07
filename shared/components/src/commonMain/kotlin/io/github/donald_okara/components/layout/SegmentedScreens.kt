package io.github.donald_okara.components.layout

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticallySegmentedScreen(
    modifier: Modifier = Modifier,
    initialSegments: List<Pair<Float, @Composable () -> Unit>>,
    minWeight: Float = 0.5f,
    enableDrag: Boolean = true
) {
    var weights by remember {
        mutableStateOf(initialSegments.map { it.first })
    }

    Column(modifier = modifier.fillMaxSize()) {
        initialSegments.forEachIndexed { index, (_, content) ->

            Box(
                modifier = Modifier
                    .weight(weights[index])
                    .fillMaxWidth()
            ) {
                 content()
            }

            if (index < initialSegments.lastIndex && enableDrag) {
                VerticalDragHandle(
                    onDrag = { delta ->
                        weights = adjustWeights(
                            weights = weights,
                            index = index,
                            delta = delta,
                            minWeight = minWeight
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun HorizontallySegmentedScreen(
    modifier: Modifier = Modifier,
    initialSegments: List<Pair<Float, @Composable () -> Unit>>,
    minWeight: Float = 0.5f,
    enableDrag: Boolean = true
) {
    var weights by remember {
        mutableStateOf(initialSegments.map { it.first })
    }

    Row(modifier = modifier.fillMaxSize()) {
        initialSegments.forEachIndexed { index, (_, content) ->

            Box(
                modifier = Modifier
                    .weight(weights[index])
                    .fillMaxHeight()
            ) {
                content()
            }

            if (index < initialSegments.lastIndex && enableDrag) {
                HorizontalDragHandle { delta ->
                    weights = adjustWeights(
                        weights,
                        index,
                        delta,
                        minWeight
                    )
                }
            }
        }
    }
}

private fun adjustWeights(
    weights: List<Float>,
    index: Int,
    delta: Float,
    minWeight: Float
): List<Float> {
    val dragFactor = delta / 300f // controls sensitivity

    val w1 = (weights[index] + dragFactor).coerceAtLeast(minWeight)
    val w2 = (weights[index + 1] - dragFactor).coerceAtLeast(minWeight)

    if (w1 + w2 <= minWeight * 2) return weights

    return weights.toMutableList().apply {
        this[index] = w1
        this[index + 1] = w2
    }
}

@Composable
private fun VerticalDragHandle(
    height: Dp = 12.dp,
    onDrag: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    onDrag(dragAmount)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HorizontalDragHandle(
    width: Dp = 12.dp,
    onDrag: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    onDrag(dragAmount)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

