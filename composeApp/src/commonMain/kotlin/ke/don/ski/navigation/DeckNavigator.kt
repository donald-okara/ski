package ke.don.ski.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import io.github.donald_okara.components.guides.keys_shortcuts.KeyEventHandler
import ke.don.domain.NavDirection
import ke.don.ski.domain.SlideConfig

class DeckNavigator(
    private val slides: List<SlideConfig>
) {
    init {
        require(slides.isNotEmpty()) { "DeckNavigator requires at least one slide." }

    }
    var direction by mutableStateOf(NavDirection.Forward)

    var currentIndex by mutableStateOf(0)
        private set

    val currentSlide: SlideConfig
        get() = slides[currentIndex]

    fun next() {
        direction = NavDirection.Forward
        if (currentIndex < slides.lastIndex) currentIndex++
    }

    fun previous() {
        direction = NavDirection.Backward
        if (currentIndex > 0) currentIndex--
    }

    fun goTo(index: Int) {
        if (index !in slides.indices || index == currentIndex) return
        direction = if (index > currentIndex) NavDirection.Forward else NavDirection.Backward
        currentIndex = index
    }
}

class DeckShortcutHandler(
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

    /**
     * Dispatches the given key event to the first registered action whose handler matches the event.
     *
     * Only processes events of type `KeyDown`; other event types are ignored.
     *
     * @param event The key event to handle.
     * @return `true` if a matching action was found and invoked, `false` otherwise.
     */
    fun handle(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        val handler = actions.keys.firstOrNull {
            it.matches(event.key)
        } ?: return false

        actions.getValue(handler).invoke()
        return true
    }
}