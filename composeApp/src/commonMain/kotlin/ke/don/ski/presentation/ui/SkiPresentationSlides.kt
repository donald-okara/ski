package ke.don.ski.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.github.donald_okara.components.timer.TimerController
import ke.don.demos.ExampleSlide
import ke.don.demos.HorizontalSegmentsDemo
import ke.don.demos.KodeViewerSlide
import ke.don.demos.VerticalSegmentsDemo
import ke.don.domain.ScreenTransition
import ke.don.introduction.IntroductionScreen
import ke.don.ski.SlidesConstants.SESSION_DURATION
import ke.don.ski.domain.SlideConfig
import ke.don.ski.domain.generateDeck
import kotlin.time.Duration

@Composable
fun skiPresentationSlides(sessionDuration: Duration = SESSION_DURATION): List<SlideConfig> {
    val timerController = rememberTimerController(sessionDuration)

    val slides = remember(timerController) {
            generateDeck(
                timerController = timerController
            ) {
                slide(
                    "Introduction",
                    transition = ScreenTransition.Fade,
                    notes = introductionNotes,
                    footer = null
                ) {
                    IntroductionScreen()
                }
                slide("Example Screen") {
                    ExampleSlide()
                }
                slide("Kode Viewer") {
                    KodeViewerSlide()
                }
                slide("Vertical Segments Demo") {
                    VerticalSegmentsDemo()
                }
                slide("Horizontal Segments Demo") {
                    HorizontalSegmentsDemo()
                }
            }
        }
    return slides
}
@Composable
fun rememberTimerController(
    sessionDuration: Duration
): TimerController {
    val scope = rememberCoroutineScope()
    return remember(scope, sessionDuration) {
        TimerController(scope, sessionDuration)
    }
}