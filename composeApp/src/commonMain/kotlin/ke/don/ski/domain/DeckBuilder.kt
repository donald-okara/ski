package ke.don.ski.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import io.github.donald_okara.components.timer.TimerController
import ke.don.domain.ScreenTransition

class DeckBuilder(
    val timerController: TimerController
) {
    private val _slides = mutableListOf<SlideConfig>()
    val slides: List<SlideConfig> get() = _slides

    fun slide(
        label: String,
        notes: List<AnnotatedString>? = null,
        transition: ScreenTransition = ScreenTransition.Horizontal,
        showHeader: Boolean = true,
        content: @Composable () -> Unit
    ) {
        _slides += SlideConfig(
            label = label,
            notes = notes,
            transition = transition,
            showHeader = showHeader,
            timer = timerController,
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