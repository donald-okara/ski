package com.example.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.demos.ExampleScreen
import com.example.introduction.IntroductionScreen
import com.example.navigation.domain.Screen

@Composable
fun ScreenSwitcher(
    modifier: Modifier = Modifier,
    screen: Screen
) {
    when (screen) {
        Screen.Introduction -> IntroductionScreen(modifier)
        Screen.ExampleScreen -> ExampleScreen(modifier)
    }
}
