package ke.don.ski.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.design.theme.AppTheme
import ke.don.design.theme.dimens
import ke.don.ski.SlidesConstants.SESSION_DURATION
import ke.don.ski.domain.DeckMode
import ke.don.ski.domain.LocalDeckMode
import ke.don.ski.domain.SlideConfig
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.presentation.ui.MainFooter
import ke.don.ski.presentation.ui.MainHeader
import ke.don.ski.presentation.ui.rememberTimerController

@Composable
fun PresentationDeck(
    slides: List<SlideConfig>,
    navigator: DeckNavigator,
    background: (@Composable () -> Unit)? = null,
    guidesFrame: SkiFrame
) {
    val deckMode = LocalDeckMode.current
    var darkMode by rememberSaveable(deckMode) { mutableStateOf(deckMode == DeckMode.Local) }

    AppTheme(
        darkTheme = darkMode,
    ) {
        Surface {
            background?.invoke()

            DeckScaffolding(
                modifier = Modifier.padding(MaterialTheme.dimens.mediumPadding),
                switchTheme = { darkMode = !darkMode },
                darkTheme = darkMode,
                frame = guidesFrame,
                slides = slides,
                navigator = navigator
            ) {
                DeckHost(
                    slides = slides,
                    navigator = navigator,
                )


                /**
                 * Uncomment this segment and set footer, header and
                 * frame params in [ke.don.ski.domain.DeckBuilder.slide] to null to use a shared frame
                 */

//                val timerController = rememberTimerController(SESSION_DURATION)
//
//                val frame = defaultSkiFrames().snake.create(Values.cornerRadius, 0.5f)
//                val timerState by timerController.state.collectAsState()
//
//                frame.Render(header = { MainHeader(deckMode) }, footer = {
//                    MainFooter(
//                        showTimer = deckMode == DeckMode.Local,
//                        label = navigator.currentSlide.label,
//                        timerState = timerState,
//                        onIntent = timerController::handleIntent,
//                        transitionSpec = navigator.contentTransform()
//
//                    )
//                }) {
//                    DeckHost(
//                        slides = slides,
//                        navigator = navigator,
//                    )
//                }
            }
        }
    }
}
