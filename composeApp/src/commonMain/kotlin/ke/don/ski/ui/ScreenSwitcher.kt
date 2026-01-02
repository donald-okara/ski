package ke.don.ski.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.don.demos.ExampleScreen
import ke.don.domain.Screen
import ke.don.introduction.IntroductionScreen

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
