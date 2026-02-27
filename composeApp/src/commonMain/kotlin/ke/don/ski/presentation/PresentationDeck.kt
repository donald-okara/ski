package ke.don.ski.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.frames.SkiFrame
import ke.don.design.theme.AppTheme
import ke.don.design.theme.dimens
import ke.don.ski.domain.SlideConfig
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.presentation.deprecated.DeckMode
import ke.don.ski.presentation.ui.skiPresentationSlides

@Composable
fun PresentationDeck(
    deckMode: DeckMode,
    slides: List<SlideConfig>,
    navigator: DeckNavigator,
    guidesFrame: SkiFrame
) {
    var darkMode by rememberSaveable(deckMode) { mutableStateOf(deckMode == DeckMode.Local) }

    AppTheme(
        darkTheme = darkMode,
    ) {
        Surface {
            DeckScaffolding(
                modifier = Modifier
                    .padding(MaterialTheme.dimens.mediumPadding),
                switchTheme = { darkMode = !darkMode },
                darkTheme = darkMode,
                frame = guidesFrame,
                mode = deckMode,
                slides = slides,
                navigator = navigator
            ){
                DeckHost(
                    slides = slides,
                    navigator = navigator
                )
            }
        }
    }
}
