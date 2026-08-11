package ke.don.domain.timer

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class TimerState(
    val status: TimerStatus = TimerStatus.Idle,
    val totalTime: Duration = 10.seconds,
    val timeLeft: Duration = totalTime
)

enum class TimerStatus { Idle, Resumed, Paused, Stopped }

sealed interface TimerIntentHandler {
    object Start: TimerIntentHandler
    object Pause: TimerIntentHandler
    object Stop: TimerIntentHandler
    object Reset: TimerIntentHandler
}
