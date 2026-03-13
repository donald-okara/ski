package ke.don.gallery.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.basic_frame.BasicFrame
import io.github.donald_okara.components.guides.notes.Notes
import io.github.donald_okara.components.guides.notes.NotesComponent
import io.github.donald_okara.components.guides.whiteboard.WhiteboardComponent
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType

fun ComponentGalleryBuilder.guides() {
    component(
        label = "Notes",
        description = "A panel for displaying slide notes and bullet points.",
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
        }
    )

    component(
        label = "Whiteboard",
        description = "A collaborative-style whiteboard component for drawing or displaying ideas.",
        type = ComponentType.Guide,
        rendered = {
            WhiteboardComponent(
                modifier = Modifier.fillMaxSize(),
                value = "",
                onValueChange = {}
            )
        }
    )
}
