package ke.don.ski

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.ski.SlidesConstants.FRAME_OPACITY
import ke.don.ski.domain.DeckMode
import ke.don.ski.domain.LocalDeckMode
import ke.don.ski.domain.SlideConfig
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.presentation.PresentationDeck
import ke.don.ski.presentation.ui.background.AnimatedDiagonalWavyBackground
import ke.don.ski.presentation.ui.background.LeftThirdCircleGrid
import ke.don.ski.presentation.ui.skiPresentationSlides
import kotlin.time.Duration.Companion.seconds

/**
 * Composable entry point that renders the slide deck UI with theme support, navigation, and frame layout.
 *
 * @param navigator Navigator responsible for slide/screen navigation within the deck.
 * @param mode Mode to use for the deck (for example local or remote presentation behavior).
 */
@Composable
fun Deck(
    mode: DeckMode = DeckMode.Local,
    slides: List<SlideConfig> = skiPresentationSlides(),
    navigator: DeckNavigator = remember { DeckNavigator(slides) },
) {
    val animatedFloat by animateFloatAsState(FRAME_OPACITY)

    val guidesFrame = defaultSkiFrames().basic.create(Values.cornerRadius, animatedFloat)

    CompositionLocalProvider(
        LocalDeckMode provides mode
    ) {
        PresentationDeck(
            guidesFrame = guidesFrame,
            background = { LeftThirdCircleGrid() },
            navigator = navigator,
            slides = slides
        )
    }
}

object SlidesConstants {
    val SESSION_DURATION = 10.seconds
    const val FRAME_OPACITY = 0.7f
}
