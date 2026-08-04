package io.github.donald_okara.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.donald_okara.components.draggable.DraggableBox

/**
 * A version of [Focusable] that allows the content to be resized and moved.
 * It uses [DraggableBox] internally for the resizing and dragging logic.
 */
@Composable
fun ResizableFocusable(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    initialWidth: Dp = 500.dp,
    initialHeight: Dp = 400.dp,
    minWidth: Dp = 250.dp,
    minHeight: Dp = 200.dp,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest
                ),
            contentAlignment = Alignment.Center
        ) {
            // Box to consume clicks so they don't reach the scrim
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Consume */ }
                )
            ) {
                DraggableBox(
                    initialWidth = initialWidth,
                    initialHeight = initialHeight,
                    minWidth = minWidth,
                    minHeight = minHeight
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        content()
                    }
                }
            }
        }
    }
}
