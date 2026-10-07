package ke.don.gallery.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ke.don.domain.frames.SkiFrame
import io.github.donald_okara.components.frames.basic_frame.BasicFrame
import io.github.donald_okara.components.guides.notes.Notes
import io.github.donald_okara.components.guides.notes.NotesComponent
import io.github.donald_okara.components.guides.whiteboard.FocusWhiteboard
import io.github.donald_okara.components.guides.whiteboard.WhiteboardCard
import io.github.donald_okara.components.guides.whiteboard.WhiteboardComponent
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType
import ke.don.gallery.domain.Focusable
import ke.don.ski.presentation.ui.FlashcardOverlay

fun ComponentGalleryBuilder.guides() {
    component(
        label = "Notes",
        description = notesDescription,
        type = ComponentType.Guide,
        rendered = {
            NotesComponent(
                notes = Notes(
                    title = "Key Takeaways",
                    points = listOf(
                        AnnotatedString("Keep your presentation concise."),
                        AnnotatedString("Use high-quality images."),
                        AnnotatedString("Engage with your audience.")
                    )
                ),
                frame = BasicFrame()
            )
        },
        dos = notesDos,
        donts = notesDonts
    )

    component(
        label = "Whiteboard",
        description = whiteboardDescription,
        type = ComponentType.Guide,
        rendered = {
            WhiteboardCard(
                modifier = Modifier.fillMaxSize(
                    fraction = 0.8f
                ).padding(
                    horizontal = 16.dp
                ),
                value = "",
                onValueChange = {}
            )
        },
        focusable = Focusable(
            path = "components.guides.whiteboard.WhiteboardComponent",
            rendered = whiteboardPreview()
        ),
        dos = whiteboardDos,
    )

    component(
        label = "Flashcards",
        description = flashcardsDescription,
        type = ComponentType.Guide,
        rendered = {
            FlashcardOverlay(
                notes = flashcardSampleNotes,
                modifier = Modifier.fillMaxSize()
            )
        },
        focusable = Focusable(
            path = "ski.presentation.ui.Flashcard.FlashcardOverlay",
            rendered = flashcardPreview()
        ),
        dos = flashcardsDos,
        donts = flashcardsDonts
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun flashcardPreview(): @Composable (onDismiss: () -> Unit) -> Unit = { onDismiss ->
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(0.8f)) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Flashcards") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )
                FlashcardOverlay(
                    notes = flashcardSampleNotes,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            }
        }
    }
}

fun whiteboardPreview(): @Composable (onDismiss: () -> Unit) -> Unit = { onDismiss ->
    var value by remember{
        mutableStateOf("")
    }
    var darkTheme by remember {
        mutableStateOf(true)
    }

    FocusWhiteboard(
        value = value,
        onValueChange = { value = it },
        onDismiss = onDismiss,
        darkTheme = darkTheme,
        toggleTheme = { darkTheme = !darkTheme }
    )
}

val notesDescription = "The Notes component is designed to present supplementary information, key takeaways, or speaker notes in a structured and easy-to-read format. It helps keep the audience focused on the most important points of a slide." +
        "\n\n" +
        "Features:" +
        "\n- Clear title section for context" +
        "\n- Bulleted list for organized information" +
        "\n- Supports AnnotatedStrings for rich text formatting" +
        "\n- Integrates seamlessly with different frame styles"

val notesDos = listOf(
    "Use bullet points to break down complex information into digestible bites",
    "Keep titles short and descriptive",
    "Use rich text formatting (bold, italics) to emphasize critical keywords",
    "Ensure there is enough white space between points for better legibility"
)

val notesDonts = listOf(
    "Avoid writing long paragraphs; use the component for concise notes instead",
    "Don't crowd the component with too many bullet points (ideally 3-5)",
    "Avoid using the Notes component as the only content on a slide if it lacks visual context"
)

val whiteboardDescription = "The Whiteboard component provides a writing area for audience interaction. It mimics the feel of a physical whiteboard, making it perfect for brainstorming sessions." +
        "\n\n" +
        "Features:" +
        "\n- Support for custom text-based ideation" +
        "\n- Responsive layout that adapts to different screen sizes"

val whiteboardDos = listOf(
    "Use for brainstorming sessions where ideas are still in flux",
    "Encourage audience interaction by using it as a shared workspace",
)

val flashcardSampleNotes = listOf(
    AnnotatedString("Ski treats slides as composables, so a deck compiles and type-checks like any other UI."),
    AnnotatedString("Reveals use state — remember and Animatable — never duplicated slides."),
    AnnotatedString("Swipe a card away to move to the next note. The stack starts over after the last card.")
)

val flashcardsDescription = "The Flashcard component turns a slide's notes into a stack of swipeable index cards, shown over the bottom-right quarter of the presenter panel. Each note becomes one card; swiping it away reveals the next." +
        "\n\n" +
        "Features:" +
        "\n- One note per card, cycling through index-card colours" +
        "\n- Swipe-to-dismiss with a spring-back if the drag doesn't clear the threshold" +
        "\n- Resets to the first card whenever the slide's notes change" +
        "\n- Toggled with F in the presenter panel, mutually exclusive with the Notes panel"

val flashcardsDos = listOf(
    "Keep each note short enough to read as a single card at a glance",
    "Use it when you want to rehearse notes hands-on instead of just reading a list",
)

val flashcardsDonts = listOf(
    "Don't rely on it as the only place notes live — it needs at least one non-blank note per slide",
    "Avoid cramming a full paragraph onto one card; split it into multiple notes instead"
)
