package ke.don.ski

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.frames.defaultSkiFrames
import ke.don.design.theme.AppTheme
import ke.don.domain.Slide
import ke.don.ski.navigation.ContainerState
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckScaffolding
import ke.don.ski.presentation.DeckMode
import ke.don.ski.presentation.MainContainer
import ke.don.ski.presentation.SlideSwitcher

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
    var darkMode by remember { mutableStateOf(false) }

    AppTheme(
        darkTheme = darkMode,
    ) {
        val frame = defaultSkiFrames().snake.create()

        Surface {
            DeckScaffolding(
                navigator = navigator,
                switchTheme = { darkMode = !darkMode },
                darkTheme = darkMode,
                frame = frame,
                mode = mode
            ) {
                MainContainer(
                    state = containerState,
                    frame = frame,
                    mode = mode
                ) { slide ->
                    SlideSwitcher(modifier = Modifier, slide = slide)
                }
            }
        }
    }
}