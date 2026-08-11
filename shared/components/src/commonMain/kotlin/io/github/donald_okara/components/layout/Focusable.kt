package io.github.donald_okara.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * A dismissable canvas where UI can be rendered, similar to a Dialog or Modal.
 * It provides a scrim (alpha-ed background) that dismisses the content when clicked.
 *
 * @param onDismissRequest Called when the scrim (background) is clicked or back button is pressed.
 * @param modifier Modifier for the outer container.
 * @param scrimColor The color of the alpha-ed background. Defaults to 60% alpha scrim from theme.
 * @param content The UI to be rendered on top of the scrim, centered by default.
 */
@Composable
fun Focusable(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false // We handle this manually with the scrim Box for full control
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
            // This Box wraps the content to prevent clicks from propagating to the background scrim
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Consume click to prevent dismissal when clicking content */ }
                )
            ) {
                content()
            }
        }
    }
}
