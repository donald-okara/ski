package ke.don.ski.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import ke.don.demos.DeviceGallery
import ke.don.demos.FeatureSlide
import ke.don.demos.FramesDemo
import ke.don.demos.HorizontalSegmentsDemo
import ke.don.demos.KodeViewerSlide
import ke.don.demos.RevealDemo
import ke.don.demos.ShortcutsSlide
import ke.don.demos.SlidesAreComposablesDemo
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
                slide("What Ski Is", notes = whatIsSkiNotes) {
                    FeatureSlide(
                        title = "What Ski Is",
                        points = listOf(
                            "A presentation framework built on Compose Multiplatform",
                            "Slides are composables, built with state and components",
                            "Runs as a browser app (Wasm) or a desktop app (JVM)"
                        )
                    )
                }
                slide("Slides Are Composables", notes = slidesAreComposablesNotes) {
                    SlidesAreComposablesDemo()
                }
                slide("Stateful Reveals", notes = stateRevealNotes) {
                    RevealDemo()
                }
                slide("Transitions", notes = transitionsNotes) {
                    FeatureSlide(
                        title = "Transitions",
                        points = listOf(
                            "Horizontal (default)",
                            "Fade",
                            "Vertical",
                            "None"
                        )
                    )
                }
                slide("Frames", notes = framesNotes) {
                    FramesDemo()
                }
                slide("Backgrounds", notes = backgroundsNotes) {
                    FeatureSlide(
                        title = "Backgrounds",
                        points = listOf(
                            "Built with BackgroundBuilder",
                            "Patterns: Wavy, DiagonalWavy, AnimatedDiagonalWavyBackground",
                            "Optional decorator image and alignment"
                        )
                    )
                }
                slide("Kode Viewer", notes = kodeViewerNotes) {
                    KodeViewerSlide()
                }
                slide("Whiteboard Screen", notes = whiteboardNotes) {
                    WhiteboardSlide()
                }
                slide("Horizontal Segments", notes = horizontalSegmentsNotes) {
                    HorizontalSegmentsDemo()
                }
                slide("Vertical Segments", notes = verticalSegmentsNotes) {
                    VerticalSegmentsDemo()
                }
                slide("Device Frames", notes = deviceFramesNotes) {
                    DeviceGallery()
                }
                slide("Presenter Mode", notes = presenterModeNotes) {
                    FeatureSlide(
                        title = "Presenter Mode",
                        points = listOf(
                            "Presenter panel: notes, timer, table of contents, shortcuts",
                            "Audience window: the slide only",
                            "Desktop windows stay in sync automatically"
                        )
                    )
                }
                slide("Keyboard Shortcuts", notes = shortcutsNotes) {
                    ShortcutsSlide()
                }
                slide("Use It in Your Project", notes = useItNotes) {
                    FeatureSlide(
                        title = "Use It in Your Project",
                        points = listOf(
                            "Add io.github.donald-okara:ski from Maven Central",
                            "Or work in this repo with local components",
                            "Write each slide as a composable in a segment"
                        )
                    )
                }
                slide("Questions", notes = questionsNotes, footer = null) {
                    FeatureSlide(
                        title = "Questions",
                        points = listOf(
                            "Live gallery: ski-gallery.vercel.app",
                            "Toolkit and docs: the README in this repo"
                        )
                    )
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
