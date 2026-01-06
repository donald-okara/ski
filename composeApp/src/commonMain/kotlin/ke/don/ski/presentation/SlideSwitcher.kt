package ke.don.ski.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import io.github.donald_okara.components.guides.notes.Notes
import ke.don.demos.ExampleSlide
import ke.don.domain.Slide
import ke.don.introduction.IntroductionScreen

@Composable
fun SlideSwitcher(
    modifier: Modifier = Modifier,
    slide: Slide,
) {
    when (slide) {
        Slide.Introduction -> IntroductionScreen(modifier)
        Slide.ExampleScreen -> ExampleSlide(modifier)
    }
}

@Composable
fun slidesNotes(
    slide: Slide,
): Notes? = when (slide) {
    Slide.Introduction -> Notes(
        title = "Introduction",
        points = listOf(
            AnnotatedString("Remember to Mention your title"),
            AnnotatedString("Keep the back story short")
        )
    )
    else -> null
}