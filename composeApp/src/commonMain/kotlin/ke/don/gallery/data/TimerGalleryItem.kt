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
        description = "A versatile timer component with wavy progress animation and multiple states.",
        type = ComponentType.Timer,
        rendered = {
            var state by remember { mutableStateOf(TimerState(timeLeft = 5.minutes, totalTime = 5.minutes, status = TimerStatus.Idle)) }
            TimerComponent(
                modifier = Modifier.padding(16.dp),
                timerState = state,
                onIntent = { /* Preview only */ }
            )
        },
        dos = listOf(
            "Use for countdowns or progress tracking",
            "Ensure the color states (red/yellow/primary) match the urgency of the timer"
        ),
        donts = listOf(
            "Avoid using for very long durations where minutes/seconds precision is not needed"
        )
    )
}
