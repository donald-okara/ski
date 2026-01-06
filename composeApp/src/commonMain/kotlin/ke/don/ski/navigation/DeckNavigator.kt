package ke.don.ski.navigation

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import io.github.donald_okara.components.guides.keys_shortcuts.KeyEventHandler
import ke.don.domain.NavDirection
import ke.don.domain.Slide

class DeckNavigator(
    val slides: List<Slide>,
    val state: ContainerState,
    val navigateForWeb: () -> Unit = {}
) {
    fun next() {
        if (state.slide.index() < slides.lastIndex) {

            state.direction = NavDirection.Forward
            state.slide = slides[state.slide.index() + 1]
            navigateForWeb()
        }
    }

    fun previous() {
        if (state.slide.index() > 0) {

            state.direction = NavDirection.Backward
            state.slide = slides[state.slide.index() - 1]
            navigateForWeb()
        }
    }

    fun jumpToScreen(slide: Slide) {
        state.direction =
            if (slide.index() > state.slide.index()) NavDirection.Forward else NavDirection.Backward
        state.slide = slide
        navigateForWeb()
    }
}

class DeckShortcutDispatcher(
    navigator: DeckNavigator,
    switchTheme: () -> Unit,
    toggleToolbar: () -> Unit,
    toggleToc: () -> Unit,
    toggleShortcutsDeck: () -> Unit,
    dismissAll: () -> Unit,
    showHint: () -> Unit,
    showNotes: () -> Unit
) {
    private val actions: Map<KeyEventHandler, () -> Unit> = mapOf(
        KeyEventHandler.Next to { navigator.next() },
        KeyEventHandler.Previous to { navigator.previous() },
        KeyEventHandler.SwitchTheme to switchTheme,
        KeyEventHandler.ShowToolBar to toggleToolbar,
        KeyEventHandler.ShowTableOfContent to toggleToc,
        KeyEventHandler.ShowShortcutGuide to toggleShortcutsDeck,
        KeyEventHandler.DismissAll to dismissAll,
        KeyEventHandler.ShowHint to showHint,
        KeyEventHandler.ShowNotes to showNotes
    )

    fun handle(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        val handler = actions.keys.firstOrNull {
            it.matches(event.key)
        } ?: return false

        actions.getValue(handler).invoke()
        return true
    }
}
