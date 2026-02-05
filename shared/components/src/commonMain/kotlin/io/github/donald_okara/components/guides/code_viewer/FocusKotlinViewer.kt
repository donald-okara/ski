package io.github.donald_okara.components.guides.code_viewer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.donald_okara.components.icon.IconButtonToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusKotlinViewer(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    darkTheme: Boolean = true,
    toggleTheme: () -> Unit = {},
    code: () -> String
) {
    /* ------------------ text scale ------------------ */

    val minScale = 0.75f
    val maxScale = 1.75f

    var textScale by rememberSaveable { mutableStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = textScale,
        label = "code-text-scale"
    )

    /* ------------------ theme ------------------ */

    val targetTheme = if (darkTheme) {
        CodeThemes.Dark
    } else {
        CodeThemes.Light
    }

    val colorScheme = animateCodeTheme(targetTheme)

    /* ------------------ code ------------------ */

    val source = code()

    val highlighted = remember(source, colorScheme) {
        highlightKotlin(source, colorScheme)
    }

    /* ------------------ dialog ------------------ */

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = modifier.fillMaxSize(0.7f),
            color = colorScheme.background,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column {

                /* ---------- top bar ---------- */

                TopAppBar(
                    title = { Text("Preview") },
                    navigationIcon = {
                        IconButtonToken(
                            icon = Icons.Default.Close,
                            accentColor = if (darkTheme) Color.White else Color.Black,
                            sizeInt = 40,
                            onClick = onDismiss
                        )
                    },
                    actions = {
                        IconButtonToken(
                            icon = if (darkTheme) {
                                Icons.Outlined.LightMode
                            } else {
                                Icons.Outlined.DarkMode
                            },
                            accentColor = if (darkTheme) Color.White else Color.Black,
                            sizeInt = 40,
                            onClick = toggleTheme
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = colorScheme.background,
                        titleContentColor = colorScheme.normal,
                    )
                )

                /* ---------- scale control ---------- */

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A",
                        style = MaterialTheme.typography.bodySmall
                            .scaled(minScale)
                    )

                    Slider(
                        value = textScale,
                        onValueChange = { textScale = it },
                        valueRange = minScale..maxScale,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "A",
                        style = MaterialTheme.typography.bodyLarge
                            .scaled(maxScale)
                    )
                }

                HorizontalDivider()

                /* ---------- content ---------- */

                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    val baseStyle = MaterialTheme.typography.bodyLarge

                    Text(
                        text = highlighted,
                        fontFamily = FontFamily.Monospace,
                        style = baseStyle.scaled(animatedScale),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

private fun TextStyle.scaled(scale: Float): TextStyle {
    return copy(
        fontSize = fontSize * scale,
        lineHeight = lineHeight * scale
    )
}

