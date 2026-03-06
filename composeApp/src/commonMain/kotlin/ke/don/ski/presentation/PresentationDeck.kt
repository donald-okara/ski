package ke.don.ski.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.backgrounds.Background
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.design.theme.AppTheme
import ke.don.design.theme.dimens
import ke.don.ski.SlidesConstants.FRAME_OPACITY
import ke.don.ski.SlidesConstants.SESSION_DURATION
import ke.don.ski.domain.DeckMode
import ke.don.ski.domain.LocalDeckMode
import ke.don.ski.domain.LocalSharesFrameFlag
import ke.don.ski.domain.SlideConfig
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.presentation.ui.MainFooter
import ke.don.ski.presentation.ui.MainHeader
import ke.don.ski.presentation.ui.rememberTimerController


/**
 * Composable that serves as the main entry point for the presentation deck.
 *
 * It manages the presentation's theme (light/dark mode), applies the base scaffolding,
 * and handles the navigation between slides.
 *
 * @param slides The list of [SlideConfig] objects defining the content of the presentation.
 * @param shareFrame Flag for whether all the slides will be rendered on the same frame
 * * If set to true, all slides will be rendered on the same frame, otherwise each slide will have its own frame.
 *
 * @param navigator The [DeckNavigator] used to manage the current slide state and transitions.
 * @param background An optional composable function to render a custom background behind the deck.
 * @param guidesFrame The [SkiFrame] used to provide structural layout and visual framing for the deck.
 */
@Composable
fun PresentationDeck(
    slides: List<SlideConfig>,
    navigator: DeckNavigator,
    background: Background? = null,
    shareFrame: Boolean = false,
    guidesFrame: SkiFrame,
    mainFrame: SkiFrame
) {
    val deckMode = LocalDeckMode.current

    var isDarkTheme by rememberSaveable(deckMode) { mutableStateOf(deckMode == DeckMode.Local) }

    CompositionLocalProvider(LocalSharesFrameFlag provides shareFrame) {
        AppTheme(
            darkTheme = isDarkTheme,
        ) {
            Surface {
                background?.Render()

                DeckScaffolding(
                    modifier = Modifier.padding(MaterialTheme.dimens.mediumPadding),
                    switchTheme = { isDarkTheme = !isDarkTheme },
                    darkTheme = isDarkTheme,
                    frame = guidesFrame,
                    slides = slides,
                    navigator = navigator
                ) {
                    if (shareFrame) {
                        val timerController = rememberTimerController(SESSION_DURATION)

                        val timerState by timerController.state.collectAsState()

                        mainFrame.Render(header = { MainHeader(deckMode) }, footer = {
                            MainFooter(
                                showTimer = deckMode == DeckMode.Local,
                                label = navigator.currentSlide.label,
                                timerState = timerState,
                                onIntent = timerController::handleIntent,
                                transitionSpec = navigator.contentTransform()

                            )
                        }) {
                            DeckHost(
                                slides = slides,
                                navigator = navigator,
                            )
                        }
                    } else {
                        DeckHost(
                            slides = slides,
                            navigator = navigator,
                        )
                    }
                }
            }
        }
    }
}
