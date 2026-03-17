package ke.don.gallery.data

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.timer.TimerComponent
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType
import ke.don.ski.presentation.ui.rememberTimerController
import kotlin.time.Duration.Companion.seconds

fun ComponentGalleryBuilder.timer() {
    component(
        label = "Timer",
        description = timerDescription,
        type = ComponentType.Timer,
        rendered = {
            val timerController = rememberTimerController(10.seconds)

            val state by timerController.state.collectAsState()

            TimerComponent(
                modifier = Modifier.padding(16.dp),
                timerState = state,
                onIntent = timerController::handleIntent
            )
        },
        dos = timerDos,
        donts = timerDonts
    )
}

val timerDescription = "A versatile timer component with wavy progress animation and multiple states. It provides visual feedback through color and movement, making it ideal for tracking time during presentations or activities." +
        "\n\n" +
        "Features:" +
        "\n- Dynamic wavy progress animation that reacts to time remaining" +
        "\n- Context-aware color states (Primary, Warning, Danger)" +
        "\n- Support for multiple statuses: Idle, Resuming, Paused, and Stopped" +
        "\n- Highly customizable duration and appearance"

val timerDos = listOf(
    "Use for countdowns, session tracking, or timed activities within your slides",
    "Leverage the color states to indicate urgency as the timer reaches its end",
    "Keep the timer visible but non-intrusive when content is being displayed",
    "Ensure the total duration is appropriate for the task being timed"
)

val timerDonts = listOf(
    "Don't clutter the UI with multiple active timers simultaneously",
    "Avoid using the 'Danger' color state for non-critical time limits"
)
