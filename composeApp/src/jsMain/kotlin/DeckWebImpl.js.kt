import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ke.don.ski.Deck
import ke.don.ski.domain.DeckMode
import ke.don.ski.domain.DeckSyncState
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.presentation.ui.skiPresentationSlides
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.w3c.dom.StorageEvent

@Composable
actual fun DeckWebImpl() {
    val slides = skiPresentationSlides()
    val navigator = remember { DeckNavigator(slides) }

    val isSlides = window.location.search.contains("slides")

    // Presenter tab writes to localStorage
    if (!isSlides) {
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
        // Notes tab listens for slide updates
        DisposableEffect(Unit) {
            val listener: (org.w3c.dom.events.Event) -> Unit = { e ->
                val event = e as? StorageEvent

                if (event?.key == "deckState") {
                    val syncState = event.newValue
                        ?.let { raw -> runCatching { Json.decodeFromString<DeckSyncState>(raw) }.getOrNull() }

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
 * Opens the presenter notes in a new browser window at "/?slides".
 *
 * The window is named "PresenterNotes" and is opened with size 800x600.
 */
fun openSlides() {
    window.open("/?slides", "PresenterNotes", "width=800,height=600")
}