package io.github.donald_okara.components.timer

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class TimerState(
    val status: TimerStatus = TimerStatus.Idle,
    val totalTime: Duration = 10.seconds,
    val timeLeft: Duration = totalTime
)