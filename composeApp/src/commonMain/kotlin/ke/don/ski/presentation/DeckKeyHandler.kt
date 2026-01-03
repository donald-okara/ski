package ke.don.ski.presentation

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
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.guides.keys_shortcuts.DeckShortcuts
import io.github.donald_okara.components.guides.keys_shortcuts.ShortcutsDictionary
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.DeckShortcutDispatcher
import kotlinx.coroutines.yield

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