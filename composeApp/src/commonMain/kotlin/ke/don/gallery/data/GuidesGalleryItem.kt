package ke.don.gallery.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.basic_frame.BasicFrame
import io.github.donald_okara.components.guides.notes.Notes
import io.github.donald_okara.components.guides.notes.NotesComponent
import io.github.donald_okara.components.guides.whiteboard.FocusWhiteboard
import io.github.donald_okara.components.guides.whiteboard.WhiteboardCard
import io.github.donald_okara.components.guides.whiteboard.WhiteboardComponent
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType
import ke.don.gallery.domain.Focusable

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
