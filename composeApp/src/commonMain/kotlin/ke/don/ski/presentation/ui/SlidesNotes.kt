package ke.don.ski.presentation.ui

import androidx.compose.ui.text.AnnotatedString

// One named constant per slide, referenced from skiPresentationSlides().
// See docs/speaker-notes.md for how to write these.

val introductionNotes = listOf(
    AnnotatedString("Welcome. This deck is a tour of Ski, and it is built with Ski, so every slide is a composable you could copy."),
    AnnotatedString("Promise the audience two things: what each feature is for, and how to build the same thing in their own deck."),
    AnnotatedString("Takeaway to return to: if you can build it in Compose, you can present it.")
)

val demoLayoutNotes = listOf(
    AnnotatedString("DemoLayout is the layout behind the demo slides: title and points on the left, a code card under them, the live demo on the right."),
    AnnotatedString("The code card shows the call that builds this slide. Point at it, then at the device frame it produces."),
    AnnotatedString("Content enters the same way on every demo slide, so the audience learns the pattern once.")
)

val videoNotes = listOf(
    AnnotatedString("Video plays in the same layout as any other demo. The player is a common composable, with a small resolver per platform."),
    AnnotatedString("The sample is bundled with the app, so it plays without a network. A remote URL works too, if the venue connection is reliable."),
    AnnotatedString("Controls hide after three seconds of playback. Tap the video to bring them back.")
)

val whatIsSkiNotes = listOf(
    AnnotatedString("Ski is a presentation framework built on Compose Multiplatform."),
    AnnotatedString("Slides are composables, so a deck is a Kotlin program rather than a document."),
    AnnotatedString("The same deck runs as a browser app (Wasm) or a desktop app (JVM).")
)

val slidesAreComposablesNotes = listOf(
    AnnotatedString("Show the DSL: each slide call takes a label, optional notes, and a composable body."),
    AnnotatedString("The code card on the right is the real snippet this slide illustrates."),
    AnnotatedString("Anything you can build in Compose can be a slide.")
)

val stateRevealNotes = listOf(
    AnnotatedString("Press Reveal to show each point. Reset starts over."),
    AnnotatedString("It is the same slide the whole time. Only state changes, so there is no duplicated slide to maintain."),
    AnnotatedString("The code card shows the three lines that make it work.")
)

val transitionsNotes = listOf(
    AnnotatedString("Each slide picks its entry transition: Horizontal (default), Fade, Vertical, or None."),
    AnnotatedString("The title uses Fade. The rest of this deck uses the default, Horizontal."),
    AnnotatedString("Set the transition on the slide, next to its label, so the choice is easy to find.")
)

val framesNotes = listOf(
    AnnotatedString("Snake is the default frame. Basic is the plain one."),
    AnnotatedString("Choose a frame with FrameBuilder, either for a single slide or for the whole deck."),
    AnnotatedString("Frames are decoration. They sit around the content and do not change its layout.")
)

val backgroundsNotes = listOf(
    AnnotatedString("The background behind the deck is built with BackgroundBuilder: a pattern, a decorator image, and an alignment."),
    AnnotatedString("Patterns are Wavy, DiagonalWavy, and AnimatedDiagonalWavyBackground."),
    AnnotatedString("The animated pattern is the one behind this deck.")
)

val kodeViewerNotes = listOf(
    AnnotatedString("This card shows a snippet of KodeViewerSlide, the composable that renders it."),
    AnnotatedString("Use the card's controls to toggle the theme and to focus the code."),
    AnnotatedString("Lambdas are folded by default, so the snippet stays short on screen.")
)

val whiteboardNotes = listOf(
    AnnotatedString("A freeform text area for annotating live, in the same card style as the code viewer."),
    AnnotatedString("Type during a demo to sketch an idea the audience can see on the slide.")
)

val horizontalSegmentsNotes = listOf(
    AnnotatedString("Three panes side by side. Drag the handles to resize them."),
    AnnotatedString("Use segmented screens when two or three things need to be compared on one slide.")
)

val verticalSegmentsNotes = listOf(
    AnnotatedString("Three panes stacked vertically. Drag the handles to resize them.")
)

val deviceFramesNotes = listOf(
    AnnotatedString("Phone mockups from DeviceCatalog: Pixel 8, Galaxy S26, and iPhone 17 Pro."),
    AnnotatedString("Put a live composable inside a device frame to show UI in context instead of a screenshot.")
)

val presenterModeNotes = listOf(
    AnnotatedString("The presenter panel shows the notes, timer, table of contents, and shortcuts. The audience window shows only the slide."),
    AnnotatedString("On desktop, the two windows share one navigator, so they stay in sync automatically."),
    AnnotatedString("On web, the audience view is a popup that follows the presenter. It works, but test it in the browser you present from.")
)

val shortcutsNotes = listOf(
    AnnotatedString("Right arrow, Space, and Enter go forward. Left arrow and Backspace go back."),
    AnnotatedString("In the presenter panel, T opens the table of contents, C the shortcut guide, and H or the down arrow the hint."),
    AnnotatedString("D switches the theme, and Escape dismisses every overlay.")
)

val useItNotes = listOf(
    AnnotatedString("The components are published to Maven Central as io.github.donald-okara:ski."),
    AnnotatedString("Or work inside this repo: set use_local_shared_components in gradle.properties."),
    AnnotatedString("Each new slide gets one named notes constant in SlidesNotes.kt.")
)

val questionsNotes = listOf(
    AnnotatedString("Take questions. Point to the live gallery and the README for the full toolkit."),
    AnnotatedString("If someone asks about a key, the shortcut guide is one press of C away.")
)
