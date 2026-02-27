package ke.don.ski

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.domain.Slide
import ke.don.ski.navigation.deprecated.ContainerState
import ke.don.ski.navigation.deprecated.DeckNavigator
import ke.don.ski.navigation.deprecated.rememberContainerState
import ke.don.ski.presentation.PresentationDeck
import ke.don.ski.presentation.deprecated.DeckMode
import ke.don.ski.presentation.deprecated.PresenterDsl

/**
 * Composable entry point that renders the slide deck UI with theme support, navigation, and frame layout.
 *
 * @param containerState State object that manages the main container's scrolling and item positioning.
 * @param navigator Navigator responsible for slide/screen navigation within the deck.
 * @param mode Mode to use for the deck (for example local or remote presentation behavior).
 */
@Composable
fun Deck(
    containerState: ContainerState = rememberContainerState(),
    navigator: DeckNavigator = remember {
        DeckNavigator(
            Slide.getScreens(), containerState
        )
    },
    mode: DeckMode = DeckMode.Local
) {
    val guidesFrame = defaultSkiFrames().basic.create(Values.cornerRadius, 0.5f)

    PresentationDeck(
        deckMode = mode,
        guidesFrame = guidesFrame,
    )
}

