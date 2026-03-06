package ke.don.ski.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.DotBullet
import io.github.donald_okara.components.LinearBullet
import io.github.donald_okara.components.timer.TimerComponent
import io.github.donald_okara.components.timer.TimerIntentHandler
import io.github.donald_okara.components.timer.TimerState
import io.github.donald_okara.components.values.Values
import ke.don.ski.domain.DeckMode


/**
 * Provides a footer composable for the current slide when the slide requests a footer.
 *
 */

@Composable
fun MainFooter(
    modifier: Modifier = Modifier,
    showTimer: Boolean,
    label: String,
    timerState: TimerState,
    opacity: Float = Values.FRAME_OPACITY,
    onIntent: (TimerIntentHandler) -> Unit,
    transitionSpec: ContentTransform? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.animateContentSize()
    ) {
        if (showTimer) {
            TimerComponent(
                timerState = timerState, onIntent = onIntent
            )
        }

        Row(
            modifier = Modifier
                .animateContentSize()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = opacity
                    ),
                    RoundedCornerShape(Values.cornerRadius)
                ).padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DotBullet()

            Spacer(Modifier.width(10.dp))

            val labelText: @Composable (String) -> Unit = { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            transitionSpec?.let {
                AnimatedContent(
                    targetState = label, transitionSpec = {
                        transitionSpec
                    }) { text ->
                    labelText(text)
                }
            } ?: labelText(label)
        }
    }
}


/**
 *
 * The rendered header shows a short title ("Ski") and a mode-dependent subtitle:
 * - If `mode` is `DeckMode.Presenter`, subtitle is "Presentation Demo" with `onSurfaceVariant` color.
 * - Otherwise, subtitle is "Presenter's panel (Do not present)" with the `error` color.
 *
 * @param mode The deck mode used to decide subtitle text and color.
 */
@Composable
fun MainHeader(
    mode: DeckMode,
    modifier: Modifier = Modifier,
    opacity: Float = Values.FRAME_OPACITY,
) {
    Row(
        modifier = modifier
            .animateContentSize()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = opacity), RoundedCornerShape(Values.cornerRadius)
            ).border(
                Values.lineThickness,
                MaterialTheme.colorScheme.onSurface,
                RoundedCornerShape(Values.cornerRadius)
            ).padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Accent rail
        LinearBullet()

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                "Ski", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold
            )
            Text(
                if (mode == DeckMode.Presenter) "Presentation Demo" else "Presenter's panel (Do not present)",
                style = MaterialTheme.typography.bodySmall,
                color = if (mode == DeckMode.Presenter) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
            )
        }
    }
}
