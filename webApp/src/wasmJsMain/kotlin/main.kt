import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.ComposeViewport
import ke.don.gallery.data.gallery
import ke.don.gallery.ui.ComponentGallery
import ke.don.ski.web.DeckWebImpl

/**
 * Press G to switch between the slide deck and the component gallery.
 *
 * Toggles on key-up rather than key-down: the browser fires repeated KeyDown
 * events while a key is held, which would otherwise flip the view back and
 * forth for as long as G stays pressed. Key-up fires exactly once per tap.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        var showGallery by remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }

        // The deck manages its own focus; the gallery doesn't, so claim focus
        // ourselves when switching to it so the G shortcut keeps working.
        LaunchedEffect(showGallery) {
            if (showGallery) focusRequester.requestFocus()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.G) {
                        showGallery = !showGallery
                        true
                    } else {
                        false
                    }
                }
        ) {
            if (showGallery) {
                ComponentGallery(gallery)
            } else {
                DeckWebImpl()
            }
        }
    }
}
