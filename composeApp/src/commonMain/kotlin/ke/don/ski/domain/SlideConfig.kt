package ke.don.ski.domain

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.timer.TimerController
import io.github.donald_okara.components.values.Values
import ke.don.design.theme.dimens
import ke.don.domain.ScreenTransition
import ke.don.ski.SlidesConstants.FRAME_OPACITY
import ke.don.ski.presentation.ui.MainFooter
import ke.don.ski.presentation.ui.MainHeader

data class SlideConfig(
    val label: String,
    val notes: List<AnnotatedString>? = null,
    val frame: @Composable () -> SkiFrame = {
        defaultSkiFrames()
            .snake
            .create(Values.cornerRadius, FRAME_OPACITY)
    },
    val transition: ScreenTransition = ScreenTransition.Horizontal,
    val showHeader: Boolean = true,
    val content: @Composable () -> Unit,
    val timer: TimerController
) {
    @Composable
    fun Render(modifier: Modifier = Modifier) {
        val timerState by timer.state.collectAsState()
        val deckMode = LocalDeckMode.current

        frame().Render(
            modifier = modifier,
            header = if (showHeader) {
                { MainHeader(mode = deckMode) }
            } else null,
            footer = {
                MainFooter(
                    showTimer = deckMode == DeckMode.Local,
                    label = label,
                    timerState = timerState,
                    onIntent = timer::handleIntent
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.dimens.mediumPadding
                    )
            ) { content() }
        }
    }
}
