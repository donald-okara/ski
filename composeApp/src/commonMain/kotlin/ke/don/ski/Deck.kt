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
import ke.don.ski.presentation.DeckKeyHandler
import ke.don.ski.presentation.DeckMode
import ke.don.ski.presentation.MainContainer
import ke.don.ski.presentation.SlideSwitcher

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
            DeckKeyHandler(
                navigator = navigator,
                switchTheme = { darkMode = !darkMode },
                darkTheme = darkMode,
                frame = frame,
                mode = mode
            ) {
                MainContainer(
                    state = containerState,
                    frame = frame
                ) { slide ->
                    SlideSwitcher(modifier = Modifier, slide = slide)
                }
            }
        }
    }
}