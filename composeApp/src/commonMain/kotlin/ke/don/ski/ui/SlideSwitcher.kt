package ke.don.ski.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ke.don.demos.ExampleSlide
import ke.don.domain.Slide
import ke.don.introduction.IntroductionScreen

@Composable
fun SlideSwitcher(
    modifier: Modifier = Modifier,
    slide: Slide
) {
    when (slide) {
        Slide.Introduction -> IntroductionScreen(modifier)
        Slide.ExampleScreen -> ExampleSlide(modifier)
    }
}
