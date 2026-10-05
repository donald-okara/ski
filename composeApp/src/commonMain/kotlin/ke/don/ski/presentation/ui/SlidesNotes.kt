package ke.don.ski.presentation.ui

import androidx.compose.ui.text.AnnotatedString

// One named constant per slide, referenced from skiPresentationSlides().
// See docs/speaker-notes.md for how to write these.

val introductionNotes = listOf(
    AnnotatedString("Remember to say hallo"),
    AnnotatedString("Say something cool")
)

val exampleScreenNotes = listOf(
    AnnotatedString("Placeholder slide: a plain Compose screen with a title and no decoration."),
    AnnotatedString("Use it to check that the deck, theme, and frame render before writing real content.")
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

val verticalSegmentsNotes = listOf(
    AnnotatedString("Three panes stacked vertically. Drag the handles to resize them.")
)

val horizontalSegmentsNotes = listOf(
    AnnotatedString("The same three panes, laid out side by side. Drag the handles to resize them."),
    AnnotatedString("Use segmented screens when two or three things need to be compared on one slide.")
)

val deviceFramesNotes = listOf(
    AnnotatedString("Phone mockups from DeviceCatalog: Pixel 8, Galaxy S26, and iPhone 17 Pro."),
    AnnotatedString("Put a live composable inside a device frame to show UI in context instead of a screenshot.")
)
