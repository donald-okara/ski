package io.github.donald_okara.components.guides.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.picture.ExpressiveFrame

/**
 * Renders a slide-note panel inside the provided SkiFrame.
 *
 * Displays the notes title (or "No notes") followed by a short divider, then either a vertically
 * spaced list of bullet points for each note or a centered message "This slide has no notes"
 * when `notes` is null.
 *
 * @param notes The notes to display. If null, a placeholder message is shown instead of bullets.
 */
@Composable
fun NotesComponent(
    modifier: Modifier = Modifier,
    notes: Notes? = null,
    frame: SkiFrame
) {
    frame.Render(
        modifier = modifier.padding(16.dp),
        header = null,
        footer = null,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = notes?.title ?: "No notes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    HorizontalDivider(
                        modifier = Modifier.width(48.dp),
                        thickness = 2.dp
                    )
                }
            }

            notes?.let {
                items(notes.points) { note ->
                    NoteBullet(text = note)
                }
            } ?: item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "This slide has no notes",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

        }
    }
}

/**
 * Renders a single bullet point consisting of a small colored circular marker and the provided text.
 *
 * @param text The content to display next to the bullet as an AnnotatedString.
 */
@Composable
private fun NoteBullet(text: AnnotatedString) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


/**
 * Displays a hint row suggesting keystrokes for showing slide notes and dismissing the hint.
 *
 * Renders the hint content inside the provided SkiFrame with 16.dp outer padding and no header or footer.
 *
 * @param frame The SkiFrame used to render the frame wrapper for the hint content.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesHint(
    modifier: Modifier = Modifier,
    frame: SkiFrame
) {
    frame.Render(
        modifier = modifier
            .padding(16.dp),
        header = null,
        footer = null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExpressiveFrame(
                polygon = MaterialShapes.Sunny,
                contentPadding = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ){
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = "Press N to show slide notes and H to dismiss Hint",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}