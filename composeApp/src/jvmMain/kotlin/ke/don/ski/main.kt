package ke.don.ski

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckMode

fun main() = application {
    // Slides / Audience window
    val containerState = rememberContainerState()

    Window(
        onCloseRequest = ::exitApplication,
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