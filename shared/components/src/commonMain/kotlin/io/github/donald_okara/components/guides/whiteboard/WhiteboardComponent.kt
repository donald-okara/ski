package io.github.donald_okara.components.guides.whiteboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.guides.code_viewer.scaled
import io.github.donald_okara.components.icon.IconButtonToken
import io.github.donald_okara.components.values.Values

@Composable
fun WhiteboardCard(
    value: String,
    onValueChange: (String) -> Unit,
    toggleFocus: () -> Unit = {},
    toggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier,
    darkTheme: Boolean = true,

    ) {
    val targetTheme = if (darkTheme) {
        WhiteboardThemes.Dark
    } else {
        WhiteboardThemes.Light
    }

    val colorScheme = animateWhiteboardTheme(targetTheme)
    Surface(
        shape = RoundedCornerShape(Values.cornerRadius),
        tonalElevation = 1.dp,
        color = colorScheme.background,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButtonToken(
                    icon = Icons.Default.Fullscreen,
                    accentColor = colorScheme.normal,
                    contentDescription = "Toggle Fullscreen",
                    sizeInt = 48,
                    onClick = toggleFocus
                )

                IconButtonToken(
                    icon = if (darkTheme) {
                        Icons.Outlined.LightMode
                    } else {
                        Icons.Outlined.DarkMode
                    },
                    contentDescription = "Toggle Theme",
                    accentColor = colorScheme.normal,
                    sizeInt = 48,
                    onClick = toggleTheme
                )

            }

            WhiteboardComponent(
                darkTheme = darkTheme,
                whiteboardTheme = colorScheme,
                value = value,
                onValueChange = onValueChange,
            )
        }
    }
}

@Composable
fun WhiteboardComponent(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = true,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Start writing...",
    whiteboardTheme: WhiteboardTheme = if (darkTheme) {
        WhiteboardThemes.Dark
    } else {
        WhiteboardThemes.Light
    },
    textScale: Float = 0.75f,
) {
    val scrollState = rememberScrollState()

    Surface(
        tonalElevation = 1.dp,
        color = whiteboardTheme.background,
        modifier = modifier
            .fillMaxSize()
            .padding(
                vertical = Values.Dimens.tinyPadding,
                horizontal = Values.Dimens.smallPadding
            ),
    ) {
        SelectionContainer {
            BasicTextField(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
                value = value,
                cursorBrush = SolidColor(whiteboardTheme.normal),
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography
                    .bodyLarge
                    .scaled(textScale)
                    .copy(color = Color.Transparent),
                decorationBox = { innerTextField ->
                    DecoratorBox(value, textScale, whiteboardTheme, placeholder, innerTextField)
                }
            )
        }
    }
}

@Composable
private fun DecoratorBox(
    value: String,
    textScale: Float,
    whiteboardTheme: WhiteboardTheme,
    placeholder: String,
    innerTextField: @Composable (() -> Unit)
) {
    Row(modifier = Modifier.fillMaxSize()) {

        // Line numbers
        Column(
            modifier = Modifier.padding(end = 12.dp),
            verticalArrangement = Arrangement.Top
        ) {
            val lineCount = value.lines().size.coerceAtLeast(1)
            repeat(lineCount) { index ->
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyLarge
                        .scaled(textScale)
                        .copy(
                            color = whiteboardTheme.normal.copy(alpha = 0.4f)
                        )
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {

            // Placeholder
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge
                        .scaled(textScale)
                        .copy(
                            color = whiteboardTheme.normal.copy(alpha = 0.3f)
                        )
                )
            }

            // Highlight links
            val annotatedText = remember(value, whiteboardTheme) {
                highlightWhiteboard(
                    text = value,
                    theme = whiteboardTheme
                )
            }

            innerTextField()

            // Overlay the styled text
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge
                    .scaled(textScale)
                    .copy(color = whiteboardTheme.normal)
            )
        }
    }
}
