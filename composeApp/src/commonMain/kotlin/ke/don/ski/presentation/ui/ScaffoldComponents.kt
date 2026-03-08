package ke.don.ski.presentation.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.guides.whiteboard.FocusWhiteboard
import ke.don.design.theme.dimens

@Composable
fun ToolBar(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    title: @Composable () -> Unit,
    onThemeClick: () -> Unit,
) {
    var whiteboardValue by remember { mutableStateOf("") }
    var showWhiteboard by remember { mutableStateOf(false) }
    var isWhiteboardDark by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(
                vertical = MaterialTheme.dimens.mediumPadding,
                horizontal = MaterialTheme.dimens.smallPadding
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            title()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ){
                IconButton(
                    onClick = { showWhiteboard = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Open Whiteboard"
                    )
                }

                IconButton(
                    onClick = onThemeClick
                ) {
                    Crossfade(targetState = darkTheme, label = "Theme icon crossfade") { isDark ->
                        if (isDark) {
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = "Switch to Light Theme"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "Switch to Dark Theme"
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp) //Nested padding is intentional here
        )
    }


    if (showWhiteboard){
        FocusWhiteboard(
            onDismiss = { showWhiteboard = false },
            darkTheme = isWhiteboardDark,
            toggleTheme = { isWhiteboardDark = !isWhiteboardDark },
            value = whiteboardValue,
            onValueChange = { whiteboardValue = it }
        )
    }
}