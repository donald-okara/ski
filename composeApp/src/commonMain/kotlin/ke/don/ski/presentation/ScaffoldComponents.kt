package ke.don.ski.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.SkiFrame
import ke.don.domain.Slide

@Composable
fun ToolBar(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    title: @Composable () -> Unit,
    onThemeClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            title()
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

        HorizontalDivider(
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp) //Nested padding is intentional here
        )
    }
}

/**
 * Renders a framed, scrollable table of contents that lists slides and allows navigating to a selected slide.
 *
 * The currently active slide is visually highlighted using the primary color, and slides marked as title screens
 * are rendered with bold text.
 *
 * @param modifier Optional modifier applied to the outer frame.
 * @param frame Frame renderer used to provide the surrounding frame for the table of contents.
 * @param slides The list of slides to display in the table.
 * @param currentSlide The slide considered active; used to determine highlight styling.
 * @param onJumpToScreen Callback invoked with the selected slide when a list item is clicked.
 */
@Composable
fun TableOfContent(
    modifier: Modifier = Modifier,
    frame: SkiFrame,
    slides: List<Slide>,
    currentSlide: Slide,
    onJumpToScreen: (Slide) -> Unit,
) {
    frame.Render(
        modifier = modifier
            .padding(16.dp),
        header = null,
        footer = null,
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(
                    vertical = 16.dp,
                    horizontal = 4.dp
                )
                .matchParentSize(),
        ) {
            item {
                Text(
                    text = "Table of content",
                    style = MaterialTheme.typography.titleMedium
                )
            }


            items(slides) { screen ->
                TextButton(
                    onClick = { onJumpToScreen(screen) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = screen.label,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (screen == currentSlide) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (screen.isTitleScreen) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

            }
        }
    }
}