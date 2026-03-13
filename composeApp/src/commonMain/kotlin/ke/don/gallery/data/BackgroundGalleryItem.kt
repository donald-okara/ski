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
        description = animatedDiagonalDescription,
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
        },
        dos = animatedDiagonalDos,
        donts = animatedDiagonalDonts
    )

    component(
        label = "Wavy Pattern",
        description = wavyPatternDescription,
        type = ComponentType.Background,
        rendered = {
            BackgroundComponent(
                pattern = Pattern.Wavy(
                    colors = listOf(Color.LightGray, Color.Gray, Color.DarkGray),
                    waveCount = 5
                ),
                decoratorImage = null
            ).Render()
        },
        dos = wavyPatternDos,
        donts = wavyPatternDonts
    )
}

val animatedDiagonalDescription = "The Animated Diagonal Wavy Background is a high-energy, dynamic background pattern featuring moving waves. It's designed to add life and motion to slides, making them more engaging for the audience." +
        "\n\n" +
        "Features:" +
        "\n- Smooth, continuous diagonal wave animation" +
        "\n- Fully customizable color palette" +
        "\n- Adjustable wave count and animation speed" +
        "\n- Performance-optimized drawing"

val animatedDiagonalDos = listOf(
    "Use for introductory slides, transitions, or high-impact sections",
    "Select colors that complement your brand or the slide's theme",
    "Keep the animation speed subtle for slides containing lots of text",
    "Use high-contrast colors if you want the background to be a primary visual element"
)

val animatedDiagonalDonts = listOf(
    "Avoid using very fast animation speeds which can be distracting or cause motion sickness",
    "Don't use overly bright or clashing colors that make the foreground text unreadable",
    "Avoid using on every single slide to maintain its visual impact"
)

val wavyPatternDescription = "The Wavy Pattern is a static, elegant background composed of layered waves. It provides a sophisticated and modern look that adds depth to slides without the potential distraction of motion." +
        "\n\n" +
        "Features:" +
        "\n- Clean and minimalist static design" +
        "\n- Layered wave effect for visual depth" +
        "\n- Lightweight and efficient for all device types"

val wavyPatternDos = listOf(
    "Ideal for content-heavy slides where motion might be distracting",
    "Use subtle, similar shades for a professional and understated look",
    "Great for internal slide containers or as a consistent theme across many slides",
    "Pair with a 'decorator image' to create a more customized branded feel"
)

val wavyPatternDonts = listOf(
    "Avoid using too many waves if it makes the background look cluttered",
    "Don't use colors that are too close to your text color, as it can reduce legibility",
    "Avoid using if you want to convey a sense of high energy or constant motion"
)
