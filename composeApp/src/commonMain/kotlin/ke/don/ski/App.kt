package ke.don.ski

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ke.don.components.frames.defaultSkiFrames
import ke.don.design.theme.AppTheme
import ke.don.domain.Slide
import ke.don.ski.navigation.DeckKeyHandler
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.ui.MainContainer
import ke.don.ski.ui.SlideSwitcher
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun App() {
    val containerState = rememberContainerState()
    val navigator = remember {
        DeckNavigator(
            Slide.getScreens(), containerState
        )
    }

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
                frame = frame
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