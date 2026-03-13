package ke.don.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.layout.LazyScatterFlow
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType

fun ComponentGalleryBuilder.layouts() {
    component(
        label = "Lazy Scatter Flow",
        description = scatterDescription,
        type = ComponentType.Layout,
        rendered = {
            LazyScatterFlow(
                items = List(3) { "Item $it" },
                itemsPerRow = 2,
                modifier = Modifier.fillMaxSize()
            ) { index, item ->
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = index.toString(), color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        },
        dos = scatterDos,
        donts = scatterDonts
    )
}

val scatterDescription = "The Lazy Scatter Flow layout is a specialized container that arranges its children in a pseudo-random, scattered pattern while maintaining a flow-like structure. It is ideal for showcasing varied content like badges, tags, or small icons in an artistic and non-rigid way." +
        "\n\n" +
        "Features:" +
        "\n- Dynamic item scattering based on a grid-like flow" +
        "\n- Configurable items per row to control density" +
        "\n- Efficiently handles lists of items lazily"

val scatterDos = listOf(
    "Use for collections of items that don't require strict alignment (e.g., skill tags, logo clouds)",
    "Vary the sizes or shapes of child elements to enhance the 'scattered' effect",
    "Keep the number of items per row low to allow for more visible scattering",
    "Use for decorative elements that should feel organic and less structured"
)

val scatterDonts = listOf(
    "Avoid using for data-heavy lists where quick scanning and readability are critical",
    "Don't use for primary navigation elements that require a predictable layout",
    "Avoid overcrowding the flow; ensure there is enough space between items to appreciate the scattering"
)
