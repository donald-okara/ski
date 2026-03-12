package ke.don.gallery.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import io.github.donald_okara.components.guides.code_viewer.FocusKotlinViewer
import io.github.donald_okara.components.guides.code_viewer.KotlinCodeViewerCard
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType
import ke.don.gallery.domain.Focusable

fun ComponentGalleryBuilder.codeViewer() {
    component(
        label = "Code Viewer",
        description = description,
        type = ComponentType.Guide,
        rendered = rendered(),
        dos = dos,
        donts = donts,
        focusable = Focusable(
            path = "components.guides.code_viewer.FocusKotlinViewer",
            rendered = focusableRendered()
        )
    )
}

private fun rendered(
    modifier: Modifier = Modifier
): @Composable () -> Unit = {
    KotlinCodeViewerCard(
        modifier = modifier
            .scale(0.6f)
    ) {
        code
    }
}

private fun focusableRendered(
    modifier: Modifier = Modifier,
): @Composable (onDismiss: () -> Unit) -> Unit = { onDismiss ->
    var darkTheme by remember {
        mutableStateOf(true)
    }
    FocusKotlinViewer(
        modifier = modifier,
        title = "Focusable Code Viewer",
        onDismiss = onDismiss,
        darkTheme = darkTheme,
        toggleTheme = { darkTheme = !darkTheme }
    ){
        code
    }
}

const val code =  "@Composable\n" +
        "fun KodeViewerSlide(\n" +
        "    modifier: Modifier = Modifier\n" +
        ") {\n" +
        "    Box(\n" +
        "        modifier = modifier\n" +
        "            .fillMaxSize(),\n" +
        "        contentAlignment = Alignment.CenterStart\n" +
        "    ) {\n" +
        "        KotlinCodeViewer()\n" +
        "    }\n" +
        "}"

val dos = listOf(
    "Use the Code Viewer to present code examples in documentation and guides",
    "Keep snippets short and focused on the concept being demonstrated",
    "Include comments in the snippet to explain important sections",
    "Verify code before publishing, as the viewer does not provide editing or compilation feedback",
    "Split large examples across multiple viewers to maintain readability"
)

val description = "The Code Viewer allows you to present Kotlin code directly in your UI without relying on screenshots." +
        "\n\n" +
        "It is designed for documentation, tutorials, and developer guides where readable code examples are required." +
        "\n\n" +
        "Features:" +
        "\n- Built-in light and dark theme switching" +
        "\n- Expandable focus mode for better readability" +
        "\n- Adjustable text size in focus mode" +
        "\n- Collapsible code blocks for long snippets"

val donts = listOf(
    "Avoid placing large or full-file implementations in a single viewer"
)