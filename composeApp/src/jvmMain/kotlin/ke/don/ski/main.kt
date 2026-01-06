package ke.don.ski

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckMode

/**
 * Starts the Compose for Desktop application and opens the Slides and Presenter Notes windows.
 *
 * The Slides window uses a fullscreen state and presents the deck in presenter mode; the
 * Presenter Notes window displays the deck in local/notes mode and closing it does not exit
 * the application. Both windows share the same container state so their views stay synchronized.
 */
fun main() = application {
    // Slides / Audience window
    val containerState = rememberContainerState()
    val windowState = WindowState(
        placement = WindowPlacement.Fullscreen
    )
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Slides"
    ) {
        Deck(
            containerState = containerState,
            mode = DeckMode.Presenter
        )
    }

    // Presenter / Notes window
    Window(
        onCloseRequest = {}, // closing notes shouldn't kill slides
        title = "Presenter Notes"
    ) {
        Deck(
            containerState = containerState,
            mode = DeckMode.Local
        )
    }
}