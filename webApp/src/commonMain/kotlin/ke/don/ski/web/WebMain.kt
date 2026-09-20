package ke.don.ski.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ke.don.ski.Deck
import ke.don.domain.DeckMode
import ke.don.domain.DeckSyncState
import ke.don.domain.DeckNavigator
import ke.don.ski.presentation.ui.skiPresentationSlides
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.w3c.dom.StorageEvent

@Composable
fun DeckWebImpl() {
    val slides = skiPresentationSlides()
    val navigator = remember { DeckNavigator(slides) }

    val isSlides = window.location.search.contains("slides")

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

fun openSlides() {
    window.open("/?slides", "PresenterNotes", "width=800,height=600")
}
