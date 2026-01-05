import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ke.don.domain.Slide
import ke.don.ski.Deck
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckMode
import kotlinx.browser.window
import org.w3c.dom.StorageEvent

@Composable
actual fun DeckWebImpl() {
    // initialize containerState, DeckNavigator
    val containerState = rememberContainerState()


    val navigator = DeckNavigator(
        slides = Slide.getScreens(),
        state = containerState,
        navigateForWeb = {
            window.localStorage.setItem("currentSlide", containerState.slide.index().toString())
        }
    )

    val isNotesTab = window.location.search.contains("notes=true")

    // Only open notes tab from the slides tab
    if (!isNotesTab) {
        LaunchedEffect(Unit) {
            openPresenterTab()
        }
    } else {
        // Notes tab listens for slide updates
        window.addEventListener("storage", { e ->
            val event = e as? StorageEvent ?: return@addEventListener
            if (event.key == "currentSlide") {
                val index = event.newValue?.toIntOrNull() ?: return@addEventListener
                containerState.slide = navigator.slides[index]
            }
        })
    }


    if (isNotesTab) {
        window.addEventListener("storage", { e ->
            val event = e as? StorageEvent ?: return@addEventListener
            if (event.key == "currentSlide") {
                val index = event.newValue?.toIntOrNull() ?: return@addEventListener
                containerState.slide = navigator.slides[index]
            }
        })

    }


    Deck(
        containerState = containerState,
        navigator = navigator,
        mode = if (isNotesTab) DeckMode.Local else DeckMode.Presenter
    )
}

// open notes tab
fun openPresenterTab() {
    window.open("/?notes=true", "PresenterNotes", "width=800,height=600")
}