package ke.don.gallery.data

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.timer.TimerComponent
import io.github.donald_okara.components.timer.TimerIntentHandler
import io.github.donald_okara.components.timer.TimerState
import io.github.donald_okara.components.timer.TimerStatus
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType
import kotlin.time.Duration.Companion.minutes

fun ComponentGalleryBuilder.timer() {
    component(
        label = "Timer",
        description = timerDescription,
        type = ComponentType.Timer,
        rendered = {
            var state by remember { mutableStateOf(TimerState(timeLeft = 5.minutes, totalTime = 5.minutes, status = TimerStatus.Idle)) }
            TimerComponent(
                modifier = Modifier.padding(16.dp),
                timerState = state,
                onIntent = { /* Preview only */ }
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
        "\n- Support for multiple statuses: Idle, Running, Paused, and Finished" +
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
