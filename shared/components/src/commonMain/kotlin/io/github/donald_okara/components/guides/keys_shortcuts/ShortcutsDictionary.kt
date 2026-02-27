package io.github.donald_okara.components.guides.keys_shortcuts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.values.Values

@Composable
fun ShortcutsDictionary(
    modifier: Modifier = Modifier,
    shortcuts: List<KeyEventHandler>,
    frame: SkiFrame
) {
    frame.Render(
        modifier = modifier
            .padding(Values.Dimens.mediumPadding),
        header = null,
        footer = null,
    ) {
        LazyColumn(
            modifier = modifier
                .matchParentSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
        ) {
            item {
                Text(
                    text = "Shortcuts",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(shortcuts) { shortcut ->
                ShortcutRow(
                    hint = shortcut.hint,
                    keys = shortcut.keys
                )
            }
        }
    }
}

@Composable
private fun ShortcutRow(
    modifier: Modifier = Modifier,
    hint: String,
    keys: Set<Key>,
) {
    // 1. Use Card for a better container with elevation and shape
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        // 2. Use a standard Row for better control over alignment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Values.Dimens.mediumPadding, vertical = Values.Dimens.smallPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                // 3. Weight modifier ensures hint takes available space, pushing keys to the end
                modifier = Modifier.weight(1f)
            )

            // 4. Use FlowRow here to allow key chips to wrap if needed on smaller screens
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding),
                verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding, Alignment.CenterVertically),
                maxItemsInEachRow = 2
            ) {
                keys.forEach { key ->
                    KeyChip(key)
                }
            }
        }
    }
}


@Composable
private fun KeyChip(key: Key) {
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(Values.Dimens.smallPadding)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(Values.Dimens.smallPadding)
            )
            .padding(horizontal = Values.Dimens.smallPadding, vertical = Values.Dimens.tinyPadding)
    ) {
        Text(
            text = key.displayName(),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

