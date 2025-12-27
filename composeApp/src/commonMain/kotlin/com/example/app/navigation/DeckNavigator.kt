package com.example.app.navigation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.example.navigation.domain.NavDirection
import com.example.navigation.domain.Screen

class DeckNavigator(
    private val screens: List<Screen>,
    val state: ContainerState
) {
    fun next() {
        if (state.screen.index() < screens.lastIndex) {

            state.screen = screens[state.screen.index() + 1]
            state.direction = NavDirection.Forward
        }
    }

    fun previous() {
        if (state.screen.index() > 0) {

            state.screen = screens[state.screen.index() - 1]
            state.direction = NavDirection.Backward
        }
    }
}

@Composable
fun DeckKeyHandler(
    navigator: DeckNavigator,
    modifier: Modifier = Modifier,
    switchTheme: () -> Unit,
    content: @Composable () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

                when (event.key) {
                    Key.DirectionRight,
                    Key.Spacebar,
                    Key.Enter -> {
                        navigator.next()
                        true
                    }

                    Key.DirectionLeft,
                    Key.Backspace -> {
                        navigator.previous()
                        true
                    }

                    Key.D -> {
                        switchTheme()
                        true
                    }

                    else -> false
                }
            }
    ) {
        content()
    }
}

