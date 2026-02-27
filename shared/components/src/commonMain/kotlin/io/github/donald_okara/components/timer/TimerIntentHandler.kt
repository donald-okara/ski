package io.github.donald_okara.components.timer

sealed interface TimerIntentHandler {
    object Start: TimerIntentHandler
    object Pause: TimerIntentHandler
    object Stop: TimerIntentHandler
    object Reset: TimerIntentHandler
}