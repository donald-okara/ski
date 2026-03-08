package io.github.donald_okara.components.guides.whiteboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.donald_okara.components.guides.code_viewer.scaled
import io.github.donald_okara.components.icon.IconButtonToken
import io.github.donald_okara.components.values.Values

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusWhiteboard(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    darkTheme: Boolean,
    toggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Whiteboard"
) {
    /* ---------- scale ---------- */

    val minScale = 0.75f
    val maxScale = 1.75f

    var textScale by rememberSaveable { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = textScale,
        label = "text-scale"
    )

    /* ---------- theme ---------- */

    val targetTheme = if (darkTheme) WhiteboardThemes.Dark else WhiteboardThemes.Light
    val colorScheme = animateWhiteboardTheme(targetTheme)

    /* ---------- dialog ---------- */

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(0.8f),
            color = colorScheme.background,
            shape = RoundedCornerShape(Values.cornerRadius)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End
            ) {

                /* --- top bar --- */

                TopAppBar(
                    title = { Text(title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.background,
                        titleContentColor = colorScheme.normal,
                        actionIconContentColor = colorScheme.normal
                    ),
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = colorScheme.normal
                            )
                        }
                    },
                    actions = {
                        IconButtonToken(
                            icon = if (darkTheme)
                                Icons.Outlined.LightMode
                            else
                                Icons.Outlined.DarkMode,
                            contentDescription = "Toggle Theme",
                            accentColor = colorScheme.normal,
                            sizeInt = 36,
                            onClick = toggleTheme
                        )
                    }
                )

                /* --- scale control --- */

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(horizontal = Values.Dimens.mediumPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A",
                        style = MaterialTheme.typography.bodySmall.scaled(minScale),
                        color = colorScheme.normal
                    )

                    Slider(
                        value = textScale,
                        onValueChange = { textScale = it },
                        valueRange = minScale..maxScale,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "A",
                        style = MaterialTheme.typography.bodyLarge.scaled(maxScale),
                        color = colorScheme.normal
                    )
                }

                HorizontalDivider()

                /* --- content --- */

                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    WhiteboardComponent(
                        value = value,
                        onValueChange = onValueChange,
                        whiteboardTheme = colorScheme,
                        textScale = animatedScale
                    )
                }
            }
        }
    }
}