package com.example.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.navigation.impl.Navigator
import com.example.navigation.transitions.horizontalSideIn
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    MaterialTheme {
        val navigator: Navigator = koinInject()
        val entryProvider = koinEntryProvider()

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            NavDisplay(
                backStack = navigator.backStack,
                modifier = Modifier.fillMaxSize(),
                entryProvider = entryProvider,
                onBack = {
                    navigator.backOrStart()
                },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                transitionSpec = {
                    horizontalSideIn()
                },
                popTransitionSpec = {
                   horizontalSideIn(true)
                },
                predictivePopTransitionSpec = {
                    horizontalSideIn(true)
                }
            )
        }
    }
}