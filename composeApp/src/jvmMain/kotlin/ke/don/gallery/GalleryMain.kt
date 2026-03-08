package ke.don.gallery

import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ke.don.gallery.data.gallery
import ke.don.gallery.ui.ComponentGallery

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Component Gallery"
    ) {
        ComponentGallery(
            components = gallery
        )
    }
}