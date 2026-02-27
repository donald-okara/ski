package ke.don.ski.presentation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.design.theme.AppTheme
import ke.don.ski.navigation.ContainerState
import ke.don.ski.navigation.DeckNavigator

@Composable
fun PresenterDsl(
    deckMode: DeckMode,
    navigator: DeckNavigator,
    containerState: ContainerState,
    guidesFrame: SkiFrame,
    presentationFrame: SkiFrame,
) {
    var darkMode by rememberSaveable(deckMode) { mutableStateOf(deckMode == DeckMode.Local) }

    AppTheme(
        darkTheme = darkMode,
    ) {
        Surface {
            DeckScaffolding(
                navigator = navigator,
                switchTheme = { darkMode = !darkMode },
                darkTheme = darkMode,
                frame = guidesFrame,
                mode = deckMode
            ) {
                MainContainer(
                    state = containerState,
                    frame = presentationFrame,
                    mode = deckMode
                ) { slide ->
                    SlideSwitcher(modifier = Modifier, slide = slide)
                }
            }
        }
    }
}