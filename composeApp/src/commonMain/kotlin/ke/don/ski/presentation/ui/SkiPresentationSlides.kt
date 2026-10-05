package ke.don.ski.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import ke.don.demos.DeviceGallery
import ke.don.demos.ExampleSlide
import ke.don.demos.HorizontalSegmentsDemo
import ke.don.demos.KodeViewerSlide
import ke.don.demos.VerticalSegmentsDemo
import ke.don.demos.WhiteboardSlide
import ke.don.domain.ScreenTransition
import ke.don.domain.SlideConfig
import ke.don.domain.SlidesConstants.SESSION_DURATION
import ke.don.domain.generateDeck
import ke.don.domain.timer.TimerController
import ke.don.introduction.IntroductionScreen
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
                slide("Example Screen", notes = exampleScreenNotes) {
                    ExampleSlide()
                }
                slide("Kode Viewer", notes = kodeViewerNotes) {
                    KodeViewerSlide()
                }
                slide("Whiteboard Screen", notes = whiteboardNotes) {
                    WhiteboardSlide()
                }
                slide("Vertical Segments Demo", notes = verticalSegmentsNotes) {
                    VerticalSegmentsDemo()
                }
                slide("Horizontal Segments Demo", notes = horizontalSegmentsNotes) {
                    HorizontalSegmentsDemo()
                }
                slide("Device Frames", notes = deviceFramesNotes) {
                    DeviceGallery()
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