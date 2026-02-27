package ke.don.ski.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.timer.TimerController
import io.github.donald_okara.components.values.Values
import ke.don.domain.ScreenTransition
import ke.don.ski.presentation.ui.MainFooter
import ke.don.ski.presentation.ui.MainHeader
import ke.don.ski.presentation.deprecated.DeckMode

data class SlideConfig(
    val label: String,
    val notes: List<AnnotatedString> = emptyList(),
    val frame: @Composable () -> SkiFrame = {
        defaultSkiFrames()
            .snake
            .create(Values.cornerRadius, 0.5f)
    },
    val transition: ScreenTransition = ScreenTransition.Horizontal,
    val showHeader: Boolean = true,
    val content: @Composable () -> Unit,
    val timer: TimerController
){
    @Composable
    fun Render(modifier: Modifier = Modifier){
        val timerState by timer.state.collectAsState()
        frame().Render(
            modifier = modifier,
            header = if (showHeader) { { MainHeader(mode = DeckMode.Local) } } else null,
            footer = {
                MainFooter(
                    showTimer = true,
                    label = label,
                    timerState = timerState,
                    onIntent = timer::handleIntent
                )
            }
        ){
            content()
        }
    }
}
