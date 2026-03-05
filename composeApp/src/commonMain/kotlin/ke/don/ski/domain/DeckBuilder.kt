package ke.don.ski.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.AnnotatedString
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.timer.TimerController
import io.github.donald_okara.components.values.Values
import ke.don.domain.ScreenTransition
import ke.don.ski.SlidesConstants.FRAME_OPACITY
import ke.don.ski.presentation.ui.MainFooter
import ke.don.ski.presentation.ui.MainHeader

class DeckBuilder(
    val timerController: TimerController
) {
    private val _slides = mutableListOf<SlideConfig>()
    val slides: List<SlideConfig> get() = _slides

    fun slide(
        label: String,
        notes: List<AnnotatedString>? = null,
        transition: ScreenTransition = ScreenTransition.Horizontal,
        frame: (@Composable () -> SkiFrame?)? = {
            val isSharedFrame = LocalSharesFrameFlag.current
            if (isSharedFrame) {
                null
            } else {
                defaultSkiFrames()
                    .snake
                    .create(Values.cornerRadius, FRAME_OPACITY)
            }
        },
        header: (@Composable () -> Unit)? = {
            val isSharedFrame = LocalSharesFrameFlag.current
            if (!isSharedFrame) {
                val deckMode = LocalDeckMode.current
                MainHeader(mode = deckMode)
            }
        },
        footer: (@Composable () -> Unit)? = {
            val isSharedFrame = LocalSharesFrameFlag.current
            if (!isSharedFrame) {
                val timerState by timerController.state.collectAsState()
                val deckMode = LocalDeckMode.current

                MainFooter(
                    showTimer = deckMode == DeckMode.Local,
                    label = label,
                    timerState = timerState,
                    onIntent = timerController::handleIntent
                )
            }
        },
        content: @Composable () -> Unit
    ) {
        _slides += SlideConfig(
            label = label,
            notes = notes,
            transition = transition,
            timer = timerController,
            frame = frame,
            footer = footer,
            header = header,
            content = content
        )
    }
}

fun generateDeck(
    timerController: TimerController,
    content: DeckBuilder.() -> Unit
): List<SlideConfig> {
    return DeckBuilder(timerController).apply(content).slides
}