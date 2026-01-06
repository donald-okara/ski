import androidx.compose.runtime.Composable
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
        LaunchedEffect(Unit) {
            window.addEventListener("storage", { e ->
                val event = e as? StorageEvent ?: return@addEventListener
                if (event.key != "deckState") return@addEventListener

                val state = event.newValue
                    ?.let { Json.decodeFromString<DeckSyncState>(it) }
                    ?: return@addEventListener

                containerState.slide = navigator.slides[state.slideIndex]
                containerState.direction = state.direction
            }
            )
        }

    }


    Deck(
        containerState = containerState,
        navigator = navigator,
        mode = if (isSlides) DeckMode.Presenter else DeckMode.Local
    )
}

// open notes tab
fun openSlides() {
    window.open("/?slides", "PresenterNotes", "width=800,height=600")
}