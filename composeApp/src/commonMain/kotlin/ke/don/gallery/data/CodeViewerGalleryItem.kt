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
        type = ComponentType.Guide,
        rendered = rendered(),

        focusable = Focusable(
            path = "components.guides.code_viewer.FocusKotlinViewer",
            rendered = focusableRendered()
        )
    )
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
