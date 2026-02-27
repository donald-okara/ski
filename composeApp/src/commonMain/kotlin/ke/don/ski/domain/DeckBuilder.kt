package ke.don.ski.domain

import androidx.compose.runtime.Composable
import ke.don.domain.ScreenTransition

class DeckBuilder {
    private val _slides = mutableListOf<SlideConfig>()
    val slides: List<SlideConfig> get() = _slides

    fun slide(
        label: String,
        notes: List<String> = emptyList(),
        transition: ScreenTransition = ScreenTransition.Horizontal,
        showHeader: Boolean = true,
        content: @Composable () -> Unit
    ) {
        _slides += SlideConfig(
            label = label,
            notes = notes,
            transition = transition,
            showHeader = showHeader,
            content = content
        )
    }
}

fun presentationDeck(
    content: DeckBuilder.() -> Unit
): List<SlideConfig> {
    return DeckBuilder().apply(content).slides
}