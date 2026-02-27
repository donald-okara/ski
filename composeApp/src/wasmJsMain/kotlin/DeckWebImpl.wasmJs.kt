import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ke.don.ski.Deck
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.deprecated.DeckSyncState
import ke.don.ski.presentation.deprecated.DeckMode
import ke.don.ski.presentation.ui.skiPresentationSlides
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.w3c.dom.StorageEvent

@Composable
actual fun DeckWebImpl() {
    val slides = skiPresentationSlides()
    val navigator = remember { DeckNavigator(slides) }

    val isSlides = window.location.search.contains("slides")

    if (!isSlides) {
        // Presenter tab writes state
        LaunchedEffect(navigator.currentIndex, navigator.direction) {
            val syncState = DeckSyncState(
                slideIndex = navigator.currentIndex,
                direction = navigator.direction
            )

            window.localStorage.setItem(
                "deckState",
                Json.encodeToString(syncState)
            )
        }

        LaunchedEffect(Unit) {
            openSlides()
        }
    } else {
        // Notes tab listens
        DisposableEffect(Unit) {
            val listener: (org.w3c.dom.events.Event) -> Unit = { e ->
                val event = e as? StorageEvent

                if (event != null && event.key == "deckState") {
                    val syncState = event.newValue
                        ?.let { Json.decodeFromString<DeckSyncState>(it) }

                    if (syncState != null && syncState.slideIndex in slides.indices) {
                        navigator.goTo(syncState.slideIndex)
                    }
                }
            }

            window.addEventListener("storage", listener)

            onDispose {
                window.removeEventListener("storage", listener)
            }
        }
    }

    Deck(
        slides = slides,
        navigator = navigator,
        mode = if (isSlides) DeckMode.Presenter else DeckMode.Local
    )
}

/**
 * Opens the presenter notes window/tab for the deck.
 *
 * The window navigates to "/?slides", uses the name "PresenterNotes" and requests a size of 800×600.
 */
fun openSlides() {
    window.open("/?slides", "PresenterNotes", "width=800,height=600")
}