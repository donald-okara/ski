package ke.don.gallery.data

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.donald_okara.components.frames.FrameBuilder
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.values.Values
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType
import ke.don.gallery.domain.Focusable

fun ComponentGalleryBuilder.frames() {

    component(
        label = "Snake Frame",
        description = """
            A frame with a snake-like border animation. This is the default frame for all Slides.
            
            Hint: The larger the frame is on the screen, the smoother the edges.
        """.trimIndent(),
        type = ComponentType.Frame,
        rendered = {
            FrameScreen(
                frame = buildFrame(isSnake = true),
                text = "Content Inside Snake Frame"
            )
        },
        focusable = Focusable(
            path = "components.frames.snake_frame.SnakeFrame",
            rendered = framesPreview(isSnake = true)
        ),
        dos = snakeDos,
        donts = snakeDonts
    )

    component(
        label = "Basic Frame",
        description = "A simple, clean outlined frame for containing content.",
        type = ComponentType.Frame,
        rendered = {
            FrameScreen(
                frame = buildFrame(isSnake = false),
                text = "Content Inside Basic Frame"
            )
        },
        focusable = Focusable(
            path = "components.frames.basic_frame.BasicFrame",
            rendered = framesPreview(isSnake = false)
        ),
        dos = basicDos
    )
}

@Composable
private fun buildFrame(isSnake: Boolean): SkiFrame =
    FrameBuilder()
        .setFrame { if (isSnake) snake else basic }
        .build()

@Composable
private fun FrameScreen(
    frame: SkiFrame,
    text: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FrameDemo(
            modifier = Modifier.scale(0.7f),
            frame = frame,
            text = text
        )
    }
}

@Composable
fun FrameDemo(
    frame: SkiFrame,
    text: String,
    modifier: Modifier = Modifier
) {
    frame.Render(
        modifier = modifier,
        header = { HeaderObject() },
        footer = { HeaderObject("Footer") }
    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun HeaderObject(text: String = "Header") {
    Surface(
        shape = RoundedCornerShape(Values.cornerRadius),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp)
        )
    }
}

fun framesPreview(
    isSnake: Boolean,
    modifier: Modifier = Modifier
): @Composable (onDismiss: () -> Unit) -> Unit = { onDismiss ->

    FramesPreview(
        frame = buildFrame(isSnake),
        text = if (isSnake) {
            "Content Inside Snake Frame"
        } else {
            "Content Inside Basic Frame"
        },
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Composable
fun FramesPreview(
    frame: SkiFrame,
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(Values.cornerRadius)
        ) {
            Box(
                modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ){
                FrameDemo(
                    frame = frame,
                    text = text,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }
            }
        }
    }
}

val snakeDos = listOf(
    "Do use for content that takes up the whole screen."
)

val snakeDonts = listOf(
    "Do not use for smaller content.",
    "Do not set headers and footers to use the maximum width."
)

val basicDos = listOf(
    "Do use for full-screen content.",
    "Do use for smaller content since it has no complex drawing.",
    "Headers and footers can safely fill the full width."
)