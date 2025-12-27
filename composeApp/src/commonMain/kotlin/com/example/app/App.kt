package com.example.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.app.navigation.DeckKeyHandler
import com.example.app.navigation.DeckNavigator
import com.example.app.navigation.rememberContainerState
import com.example.app.ui.MainContainer
import com.example.app.ui.ScreenSwitcher
import com.example.design.ui.theme.AppTheme
import com.example.navigation.domain.Screen

@Composable
fun App() {
    val containerState = rememberContainerState()
    val navigator = remember {
        DeckNavigator(
            Screen.getScreens(),
            containerState
        )
    }

    var darkMode by remember { mutableStateOf(false) }

    AppTheme(
        darkTheme = darkMode
    ) {
        Surface {
            DeckKeyHandler(
                navigator = navigator,
                switchTheme = { darkMode = !darkMode }
            ){
                MainContainer(
                    state = containerState,
                ) { screen ->
                    ScreenSwitcher(modifier = Modifier, screen = screen)
                }
            }
        }
    }
}