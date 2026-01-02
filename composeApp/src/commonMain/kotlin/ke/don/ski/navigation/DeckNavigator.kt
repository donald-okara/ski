package ke.don.ski.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import ke.don.components.frames.SkiFrame
import ke.don.components.guides.keys_shortcuts.DeckShortcuts
import ke.don.components.guides.keys_shortcuts.KeyEventHandler
import ke.don.components.guides.keys_shortcuts.ShortcutsDictionary
import ke.don.components.layout.TableOfContent
import ke.don.components.layout.ToolBar
import ke.don.domain.NavDirection
import ke.don.domain.Slide
import ke.don.ski.ui.transitionFor
import kotlinx.coroutines.yield

class DeckNavigator(
    val slides: List<Slide>,
    val state: ContainerState,
) {
    fun next() {
        if (state.slide.index() < slides.lastIndex) {

            state.direction = NavDirection.Forward
            state.slide = slides[state.slide.index() + 1]
        }
    }

    fun previous() {
        if (state.slide.index() > 0) {

            state.direction = NavDirection.Backward
            state.slide = slides[state.slide.index() - 1]
        }
    }

    fun jumpToScreen(slide: Slide) {
        state.direction =
            if (slide.index() > state.slide.index()) NavDirection.Forward else NavDirection.Backward
        state.slide = slide
    }
}

class DeckShortcutDispatcher(
    navigator: DeckNavigator,
    switchTheme: () -> Unit,
    toggleToolbar: () -> Unit,
    toggleToc: () -> Unit,
    toggleShortcutsDeck: () -> Unit,
    dismissAll: () -> Unit
) {
    private val actions: Map<KeyEventHandler, () -> Unit> = mapOf(
        KeyEventHandler.Next to { navigator.next() },
        KeyEventHandler.Previous to { navigator.previous() },
        KeyEventHandler.SwitchTheme to switchTheme,
        KeyEventHandler.ShowToolBar to toggleToolbar,
        KeyEventHandler.ShowTableOfContent to toggleToc,
        KeyEventHandler.ShowShortcutGuide to toggleShortcutsDeck,
        KeyEventHandler.DismissAll to dismissAll
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

@Composable
fun DeckKeyHandler(
    navigator: DeckNavigator,
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    frame: SkiFrame,
    switchTheme: () -> Unit,
    content: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var showToolBar by remember { mutableStateOf(false) }
    var showTableOfContent by remember { mutableStateOf(false) }
    var showShortcuts by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }

    val shortcutDispatcher = remember(navigator) {
        DeckShortcutDispatcher(
            navigator = navigator,
            switchTheme = switchTheme,
            toggleToolbar = { showToolBar = !showToolBar },
            toggleToc = {
                showTableOfContent = !showTableOfContent
                showShortcuts = false
            },
            toggleShortcutsDeck = {
                showShortcuts = !showShortcuts
                showTableOfContent = false
            },
            dismissAll = {
                showTableOfContent = false
                showShortcuts = false
                showToolBar = false
            }
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(hasFocus) {
        if (!hasFocus) {
            // small delay avoids fighting child focus transitions
            yield()
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onFocusChanged { state ->
                hasFocus = state.hasFocus || state.isFocused
            }
            .onKeyEvent(shortcutDispatcher::handle)
    ) {
        AnimatedVisibility(showToolBar) {
            ToolBar(
                darkTheme = darkTheme,
                onThemeClick = switchTheme,
                title = {
                    AnimatedContent(
                        targetState = navigator.state.slide.label,
                        transitionSpec = {
                            transitionFor(
                                slide = navigator.state.slide,
                                direction = navigator.state.direction
                            )
                        },
                        label = "text-change"
                    ) { value ->
                        Text(
                            text = value,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                }
            )
        }

        Row(Modifier.weight(1f)) {
            AnimatedVisibility(showTableOfContent) {
                TableOfContent(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight(),
                    slides = navigator.slides,
                    currentSlide = navigator.state.slide,
                    onJumpToScreen = { navigator.jumpToScreen(it) },
                    frame = frame
                )
            }
            Box(Modifier.weight(1f)) {
                content()
            }

            AnimatedVisibility(showShortcuts) {
                ShortcutsDictionary(
                    shortcuts = DeckShortcuts,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(400.dp),
                    frame = frame
                )
            }
        }
    }
}
