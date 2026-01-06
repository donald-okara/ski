package ke.don.ski

import DeckWebImpl
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ke.don.domain.Slide
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.rememberContainerState

/**
 * Application entry point that creates a Compose viewport and hosts the DeckWebImpl UI.
 *
 * Launches a ComposeViewport and composes DeckWebImpl as the viewport's content.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        DeckWebImpl()
    }
}
