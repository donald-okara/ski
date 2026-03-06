package ke.don.gallery

import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Component Gallery"
    ) {
        // Replace this with your Gallery UI
        Text("Component Gallery")
    }
}