package ke.don.ski.presentation.ui

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
import ke.don.design.theme.dimens
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