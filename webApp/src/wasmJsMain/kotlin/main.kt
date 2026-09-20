import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import ke.don.gallery.data.gallery
import ke.don.gallery.ui.ComponentGallery
import ke.don.ski.web.DeckWebImpl

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        var showGallery by remember { mutableStateOf(true) }

        Box(modifier = Modifier.fillMaxSize()) {
            if (showGallery) {
                ComponentGallery(gallery)
            } else {
                DeckWebImpl()
            }

            FloatingActionButton(
                onClick = { showGallery = !showGallery },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (showGallery) Icons.Default.Slideshow else Icons.Default.GridView,
                    contentDescription = if (showGallery) "Switch to Slides" else "Switch to Gallery"
                )
            }
        }
    }
}
