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
        description = snakeDescription,
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
        description = basicDescription,
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
        dos = basicDos,
        donts = basicDonts
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

val snakeDescription = "The Snake Frame is a visually engaging container featuring a dynamic, animated 'snake' border. It's designed to draw attention to the slide content and provide a modern, interactive feel to your presentation." +
        "\n\n" +
        "Features:" +
        "\n- Smooth, continuous border animation" +
        "\n- Supports custom headers and footers" +
        "\n- Automatically scales its drawing complexity based on size"

val snakeDos = listOf(
    "Use for primary content that occupies the majority of the slide",
    "Keep headers and footers concise to avoid interfering with the border animation",
    "Use high-contrast colors for the snake to make it pop against the background"
)

val snakeDonts = listOf(
    "Avoid using for very small components or tooltips where the animation might be distracting",
    "Don't set headers or footers to occupy the full width, as they may overlap the animated border",
    "Avoid using multiple snake frames on a single slide to prevent visual clutter"
)

val basicDescription = "The Basic Frame is a clean and minimalist container with a standard outlined border. It is highly versatile and serves as the perfect foundation for any type of slide content without distracting from the message." +
        "\n\n" +
        "Features:" +
        "\n- Lightweight and performant" +
        "\n- Consistent with Material Design 3 principles" +
        "\n- Flexible header and footer placement"

val basicDos = listOf(
    "Use for informational slides where the content should be the primary focus",
    "Ideal for nested containers or smaller UI elements within a slide",
    "Feel free to use full-width headers and footers for a structured look"
)

val basicDonts = listOf(
    "Avoid using when a more high-energy or decorative feel is desired for the slide",
    "Don't over-rely on basic frames if the presentation needs more visual variety"
)
