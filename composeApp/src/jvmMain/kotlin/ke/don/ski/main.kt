package ke.don.ski

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckMode

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
        state = windowState,
        title = "Presenter Notes"
    ) {
        Deck(
            containerState = containerState,
            mode = DeckMode.Local
        )
    }
}