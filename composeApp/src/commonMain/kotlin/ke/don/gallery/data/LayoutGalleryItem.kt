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
        description = "A layout that scatters items in a flow-like manner, useful for varied content distribution.",
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
        }
    )
}
