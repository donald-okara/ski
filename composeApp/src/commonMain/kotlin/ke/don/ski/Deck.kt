package ke.don.ski

import androidx.compose.runtime.Composable
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.ski.domain.SlideConfig
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.deprecated.ContainerState
import ke.don.ski.navigation.deprecated.rememberContainerState
import ke.don.ski.presentation.PresentationDeck
import ke.don.ski.presentation.deprecated.DeckMode
import ke.don.ski.presentation.ui.skiPresentationSlides

/**
 * Composable entry point that renders the slide deck UI with theme support, navigation, and frame layout.
 *
 * @param navigator Navigator responsible for slide/screen navigation within the deck.
 * @param mode Mode to use for the deck (for example local or remote presentation behavior).
 */
@Composable
fun Deck(
    slides: List<SlideConfig> = skiPresentationSlides(),
    navigator: DeckNavigator = DeckNavigator(slides),
    mode: DeckMode = DeckMode.Local
) {
    val guidesFrame = defaultSkiFrames().basic.create(Values.cornerRadius, 0.5f)

    PresentationDeck(
        deckMode = mode,
        guidesFrame = guidesFrame,
        slides = slides,
        navigator = navigator
    )
}

