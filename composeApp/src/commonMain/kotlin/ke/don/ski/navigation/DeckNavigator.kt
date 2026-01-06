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
    /**
     * Advance the current slide to the next slide if not already on the last slide.
     *
     * Sets the navigation direction to Forward, updates the shared state to the following slide,
     * and invokes the `navigateForWeb` callback after the change.
     */
    fun next() {
        if (state.slide.index() < slides.lastIndex) {

            state.direction = NavDirection.Forward
            state.slide = slides[state.slide.index() + 1]
            navigateForWeb()
        }
    }

    /**
     * Moves the current slide to the previous slide when one exists.
     *
     * Updates the shared navigation state to indicate backward movement and sets the current slide
     * to the preceding slide in the list, then invokes the web navigation callback.
     *
     * No action is taken if the current slide is the first slide.
     */
    fun previous() {
        if (state.slide.index() > 0) {

            state.direction = NavDirection.Backward
            state.slide = slides[state.slide.index() - 1]
            navigateForWeb()
        }
    }

    /**
     * Navigate directly to the specified slide, updating the navigation direction based on
     * the target slide's index relative to the current slide, setting the current slide, and
     * invoking the web navigation callback.
     *
     * @param slide The target slide to navigate to.
     */
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