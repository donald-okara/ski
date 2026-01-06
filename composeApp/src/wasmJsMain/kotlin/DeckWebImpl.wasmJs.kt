import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import ke.don.domain.Slide
import ke.don.ski.Deck
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.DeckSyncState
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckMode
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.w3c.dom.StorageEvent

@Composable
actual fun DeckWebImpl() {
    // initialize containerState, DeckNavigator
    val containerState = rememberContainerState()


    val navigator = DeckNavigator(
        slides = Slide.getScreens(),
        state = containerState,
        navigateForWeb = {
            val syncState = DeckSyncState(
                slideIndex = containerState.slide.index(),
                direction = containerState.direction
            )

            window.localStorage.setItem(
                "deckState",
                Json.encodeToString(syncState)
            )
        }
    )

    val isSlides = window.location.search.contains("slides")

    // Only open notes tab from the slides tab
    if (!isSlides) {
        LaunchedEffect(Unit) {
            openSlides()
        }
    } else {
        // Notes tab listens for slide updates
        DisposableEffect(Unit) {
            val listener: (org.w3c.dom.events.Event) -> Unit = { e ->
                val event = e as? StorageEvent

                event?.newValue
                    ?.let { Json.decodeFromString<DeckSyncState>(it) }
                    ?.takeIf { it.slideIndex in navigator.slides.indices }
                    ?.let { syncState ->
                        containerState.slide = navigator.slides[syncState.slideIndex]
                        containerState.direction = syncState.direction
                    }
            }
            window.addEventListener("storage", listener)

            onDispose {
                window.removeEventListener("storage", listener)
            }

        }
    }


    Deck(
        containerState = containerState,
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