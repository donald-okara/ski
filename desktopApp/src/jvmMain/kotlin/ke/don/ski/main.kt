package ke.don.ski

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ke.don.domain.DeckMode
import ke.don.domain.DeckNavigator
import ke.don.ski.presentation.ui.skiPresentationSlides

fun main() = application {
    val slides = skiPresentationSlides()
    val navigator = remember { DeckNavigator(slides) }

    val windowState = WindowState(
        placement = WindowPlacement.Fullscreen
    )
    val doubleLaunch = true

    if (doubleLaunch) {
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Slides"
        ) {
            Deck(
                mode = DeckMode.Presenter,
                slides = slides,
                navigator = navigator
            )
        }
    }

    Window(
        onCloseRequest = { if(doubleLaunch.not()) exitApplication() },
        title = "Presenter Notes"
    ) {
        Deck(
            mode = DeckMode.Local,
            slides = slides,
            navigator = navigator
        )
    }
}
