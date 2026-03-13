package ke.don.gallery.data

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.backgrounds.BackgroundComponent
import io.github.donald_okara.components.backgrounds.pattern.Pattern
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType

fun ComponentGalleryBuilder.backgrounds() {
    component(
        label = "Animated Diagonal Wavy Background",
        description = "A dynamic background pattern with animated diagonal waves. It is best used for slides backgrounds and interactive presentations.",
        type = ComponentType.Background,
        rendered = {
            BackgroundComponent(
                pattern = Pattern.AnimatedDiagonalWavyBackground(
                    colors = listOf(Color(0xFFFF6E40), Color(0xFF1E88E5), Color(0xFF43A047)),
                    waveCount = 3,
                    animationDuration = 2000
                ),
                decoratorImage = null
            ).Render()
        }
    )

    component(
        label = "Wavy Pattern",
        description = "A static wavy pattern background. It is best used for simple backgrounds and interactive presentations.",
        type = ComponentType.Background,
        rendered = {
            BackgroundComponent(
                pattern = Pattern.Wavy(
                    colors = listOf(Color.LightGray, Color.Gray, Color.DarkGray),
                    waveCount = 5
                ),
                decoratorImage = null
            ).Render()
        }
    )
}
