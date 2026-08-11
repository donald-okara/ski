package ke.don.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import ke.don.domain.frames.SkiFrame
import ke.don.domain.timer.TimerController
import ke.don.domain.values.Values
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object SlidesConstants {
    val SESSION_DURATION = 45.minutes
    const val FRAME_OPACITY = Values.FRAME_OPACITY
}

class DeckBuilder(
    val timerController: TimerController
) {
    private val _slides = mutableListOf<SlideConfig>()
    val slides: List<SlideConfig> get() = _slides

    fun slide(
        label: String,
        notes: List<AnnotatedString>? = null,
        transition: ScreenTransition = ScreenTransition.Horizontal,
        frame: (@Composable () -> SkiFrame?)? = null,
        header: (@Composable () -> Unit)? = null,
        footer: (@Composable () -> Unit)? = null,
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
