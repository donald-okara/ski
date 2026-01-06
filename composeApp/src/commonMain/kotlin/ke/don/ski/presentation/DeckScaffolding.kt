package ke.don.ski.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.guides.keys_shortcuts.DeckShortcuts
import io.github.donald_okara.components.guides.keys_shortcuts.ShortcutsDictionary
import io.github.donald_okara.components.guides.notes.Notes
import io.github.donald_okara.components.guides.notes.NotesComponent
import io.github.donald_okara.components.guides.notes.NotesHint
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.DeckShortcutDispatcher
import kotlinx.coroutines.yield

enum class DeckMode { Presenter, Local }

@Composable
fun DeckScaffolding(
    navigator: DeckNavigator,
    mode: DeckMode,
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    frame: SkiFrame? = null, // only needed for Local mode
    switchTheme: () -> Unit,
    notes: Notes? = slidesNotes(navigator.state.slide),
    content: @Composable () -> Unit,
) {
    var showToolBar by remember { mutableStateOf(false) }
    var showTableOfContent by remember { mutableStateOf(false) }
    var showShortcuts by remember { mutableStateOf(false) }

    var showNotes by remember { mutableStateOf(true) }
    var showHint by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

    val shortcutDispatcher = remember(navigator, mode) {
        DeckShortcutDispatcher(
            navigator = navigator,
            switchTheme = switchTheme,
            toggleToolbar = { showToolBar = !showToolBar },
            toggleToc = {
                if (mode == DeckMode.Local) {
                    showTableOfContent = !showTableOfContent
                    showShortcuts = false
                    showNotes = false
                    showHint = false
                }
            },
            toggleShortcutsDeck = {
                if (mode == DeckMode.Local) {
                    showShortcuts = !showShortcuts
                    showTableOfContent = false
                    showNotes = false
                    showHint = false
                }
            },
            dismissAll = {
                showToolBar = false
                if (mode == DeckMode.Local) {
                    showHint = false
                    showNotes = false
                    showTableOfContent = false
                    showShortcuts = false
                }
            },
            showHint = {
                if (mode == DeckMode.Local) {
                    showHint = !showHint
                }
            },
            showNotes = {
                if (mode == DeckMode.Local) {
                    showNotes = !showNotes
                    showShortcuts = false
                    showTableOfContent = false
                    showHint = false
                }

            }
        )
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LaunchedEffect(hasFocus) {
        if (!hasFocus) {
            yield()
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onFocusChanged { state -> hasFocus = state.hasFocus || state.isFocused }
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

        if (mode == DeckMode.Presenter) {
            Box(Modifier.weight(1f)) { content() }
        } else {
            // Local mode layout with side panels
            Row(Modifier.weight(1f)) {
                AnimatedVisibility(showTableOfContent) {
                    TableOfContent(
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight(),
                        slides = navigator.slides,
                        currentSlide = navigator.state.slide,
                        onJumpToScreen = { navigator.jumpToScreen(it) },
                        frame = frame!!
                    )
                }

                Box(Modifier.weight(1f)) { content() }

                AnimatedVisibility(showShortcuts) {
                    ShortcutsDictionary(
                        shortcuts = DeckShortcuts,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(400.dp),
                        frame = frame!!
                    )
                }

                AnimatedVisibility(showNotes) {
                    NotesComponent(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(400.dp),
                        notes = notes,
                        frame = frame!!
                    )
                }
            }
        }

        AnimatedVisibility(showHint && mode == DeckMode.Local) {
            NotesHint(
                modifier = Modifier
                    .height(100.dp),
                frame = frame!!
            )
        }
    }
}
