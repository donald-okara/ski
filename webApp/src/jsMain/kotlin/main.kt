import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ke.don.ski.web.DeckWebImpl

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        DeckWebImpl()
    }
}
