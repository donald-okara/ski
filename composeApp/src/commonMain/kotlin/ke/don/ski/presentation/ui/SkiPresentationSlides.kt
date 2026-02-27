package ke.don.ski.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import io.github.donald_okara.components.timer.TimerController
import ke.don.demos.ExampleSlide
import ke.don.demos.HorizontalSegmentsDemo
import ke.don.demos.KodeViewerSlide
import ke.don.demos.VerticalSegmentsDemo
import ke.don.introduction.IntroductionScreen
import ke.don.ski.domain.SlideConfig
import ke.don.ski.domain.generateDeck

@Composable
fun skiPresentationSlides(): List<SlideConfig> {
    val timerController = rememberTimerController()

    val slides = generateDeck(
        timerController = timerController
    ) {
        slide(
            "Introduction"
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
    return slides
}
@Composable
fun rememberTimerController(): TimerController {
    val scope = rememberCoroutineScope()
    return remember(scope) {
        TimerController(scope)
    }
}