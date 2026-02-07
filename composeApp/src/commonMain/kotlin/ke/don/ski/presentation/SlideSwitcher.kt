package ke.don.ski.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import io.github.donald_okara.components.guides.notes.Notes
import ke.don.demos.ExampleSlide
import ke.don.demos.HorizontalSegmentsDemo
import ke.don.demos.KodeViewerSlide
import ke.don.demos.VerticalSegmentsDemo
import ke.don.domain.Slide
import ke.don.introduction.IntroductionScreen

/**
 * Displays the UI screen corresponding to the provided slide.
 *
 * @param modifier Modifier to be applied to the displayed screen.
 * @param slide The Slide value that determines which composable screen to show.
 */
@Composable
fun SlideSwitcher(
    modifier: Modifier = Modifier,
    slide: Slide,
) {
    when (slide) {
        Slide.Introduction -> IntroductionScreen(modifier)
        Slide.ExampleScreen -> ExampleSlide(modifier)
        Slide.KodeViewer -> KodeViewerSlide(modifier)
        Slide.VerticalSegmentsDemo -> VerticalSegmentsDemo(modifier)
        Slide.HorizontalSegmentsDemo -> HorizontalSegmentsDemo(modifier)
    }
}

/**
 * Provide speaker notes for the specified slide.
 *
 * @param slide The slide to produce notes for.
 * @return `Notes` for the specified slide, or `null` if no notes are available.
 */
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