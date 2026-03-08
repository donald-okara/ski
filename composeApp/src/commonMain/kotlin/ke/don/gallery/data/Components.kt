package ke.don.gallery.data

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import ke.don.gallery.domain.componentGallery

val gallery = componentGallery {
    component(
        label = "Primary Button",
        rendered = { Button(onClick = {}){Text("Click me")} },
        description = "Used for primary actions in a screen",
        dos = listOf("Use for main actions", "Keep text short"),
        donts = listOf("Don't use for secondary actions")
    )

    component(
        label = "Text Field",
        rendered = { TextField(value = "", onValueChange = {}) },
        description = "Input field for user text",
        dos = listOf("Provide placeholder text"),
        donts = listOf("Don't leave empty labels")
    )
}