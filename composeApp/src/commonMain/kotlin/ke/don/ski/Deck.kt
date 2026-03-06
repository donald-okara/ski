package ke.don.ski

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import io.github.donald_okara.components.backgrounds.BackgroundBuilder
import io.github.donald_okara.components.backgrounds.decorator_image.DecoratorImage
import io.github.donald_okara.components.backgrounds.pattern.Pattern
import io.github.donald_okara.components.backgrounds.pattern.PatternDefaults
import io.github.donald_okara.components.frames.FrameBuilder
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.values.Values
import ke.don.resources.Resources
import ke.don.ski.SlidesConstants.FRAME_OPACITY
import ke.don.ski.domain.DeckMode
import ke.don.ski.domain.LocalDeckMode
import ke.don.ski.domain.SlideConfig
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.presentation.PresentationDeck
import ke.don.ski.presentation.ui.skiPresentationSlides
import kotlin.time.Duration.Companion.seconds

/**
 * Composable entry point that renders the slide deck UI with theme support, navigation, and frame layout.
 *
 * @param navigator Navigator responsible for slide/screen navigation within the deck.
 * @param mode Mode to use for the deck (for example local or remote presentation behavior).
 * @param slides List of slide configurations to display in the deck.
 */
@Composable
fun Deck(
    mode: DeckMode = DeckMode.Local,
    slides: List<SlideConfig> = skiPresentationSlides(),
    navigator: DeckNavigator = remember { DeckNavigator(slides) },
) {

    val guidesFrame = FrameBuilder()
        .setFrame { basic }
        .setOpacity(FRAME_OPACITY)
        .build()

    val mainFrame = FrameBuilder()
        .setFrame { snake }
        .setOpacity(FRAME_OPACITY)
        .build()

    val background = BackgroundBuilder()
        .setDecoratorImage(DecoratorImage(Resources.Images.ANDROID_ROBOT))
        .setPattern(pattern = Pattern.AnimatedDiagonalWavyBackground(colors = PatternDefaults.colors))
        .build()


    CompositionLocalProvider(
        LocalDeckMode provides mode
    ) {
        PresentationDeck(
            mainFrame = mainFrame,
            guidesFrame = guidesFrame,
            background = background,
            navigator = navigator,
            slides = slides,
            shareFrame = true
        )
    }
}

object SlidesConstants {
    val SESSION_DURATION = 10.seconds
    const val FRAME_OPACITY = Values.FRAME_OPACITY
}
