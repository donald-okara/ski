package io.github.donald_okara.components.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimerController(
    private val scope: CoroutineScope,
    totalDuration: Duration = 10.seconds
) {

    private val _state = MutableStateFlow(TimerState(totalTime = totalDuration))
    val state = _state.asStateFlow()

    private var tickingJob: Job? = null

    fun handleIntent(intentHandler: TimerIntentHandler){
        when(intentHandler){
            TimerIntentHandler.Start -> onStart()
            TimerIntentHandler.Pause -> onPause()
            TimerIntentHandler.Stop -> onStop()
            TimerIntentHandler.Reset -> onReset()
        }
    }

    fun onStart() {
        if (_state.value.status == TimerStatus.Resumed) return

        _state.update { it.copy(status = TimerStatus.Resumed) }
        startTicking()
    }

    fun onPause() {
        _state.update { it.copy(status = TimerStatus.Paused) }
        stopTicking()
    }

    fun onStop() {
        _state.update { it.copy(status = TimerStatus.Stopped) }
        stopTicking()
    }

    fun onReset() {
        stopTicking()
        _state.update {
            it.copy(
                timeLeft = it.totalTime,
                status = TimerStatus.Idle
            )
        }
    }

    private fun startTicking() {
        tickingJob?.cancel()

        tickingJob = scope.launch {
            while (_state.value.status == TimerStatus.Resumed &&
                _state.value.timeLeft > Duration.ZERO
            ) {
                delay(1.seconds)
                onTick()
            }

            if (_state.value.timeLeft <= Duration.ZERO) {
                onStop()
            }
        }
    }

    private fun stopTicking() {
        tickingJob?.cancel()
        tickingJob = null
    }

    private fun onTick() {
        _state.update {
            it.copy(timeLeft = (it.timeLeft - 1.seconds).coerceAtLeast(Duration.ZERO))
        }
    }
}