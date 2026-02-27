package io.github.donald_okara.components.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.values.Values
import kotlin.time.Duration


@Composable
fun TimerComponent(
    modifier: Modifier = Modifier, timerState: TimerState, onIntent: (TimerIntentHandler) -> Unit
) {
    // Fraction 0f..1f
    val progress = (timerState.timeLeft / timerState.totalTime).coerceIn(0.toDouble(), 1.toDouble())
    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat(),
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            Values.Dimens.smallPadding, Alignment.CenterHorizontally
        )
    ) {
        TickerSegment(Modifier, timerState, onIntent, animatedProgress, progress)

        AnimatedVisibility(
            visible = timerState.status == TimerStatus.Paused
        ) {
            Surface(
                onClick = { onIntent(TimerIntentHandler.Reset) },
                color = Color.Black,
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Replay,
                    contentDescription = "Reset",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(Values.Dimens.smallPadding)
                )
            }
        }
    }
}

@Composable
private fun TickerSegment(
    modifier: Modifier,
    timerState: TimerState,
    onIntent: (TimerIntentHandler) -> Unit,
    animatedProgress: Float,
    progress: Double
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.animateContentSize().widthIn(min = 160.dp).height(64.dp)
            .clip(RoundedCornerShape(Values.cornerRadius)).background(Color.DarkGray).clickable {
                when (timerState.status) {
                    TimerStatus.Idle -> onIntent(TimerIntentHandler.Start)
                    TimerStatus.Resumed -> onIntent(TimerIntentHandler.Pause)
                    TimerStatus.Paused -> onIntent(TimerIntentHandler.Start)
                    TimerStatus.Stopped -> onIntent(TimerIntentHandler.Reset)
                }
            }) {
        // Wavy vertical fill
        WavyVerticalProgress(
            waveCount = 3,
            progress = animatedProgress,
            modifier = Modifier.matchParentSize(),
            colors = when {
                (timerState.status == TimerStatus.Stopped) || (timerState.status == TimerStatus.Idle) || (timerState.status == TimerStatus.Paused) -> listOf(
                    Color.Transparent, Color.DarkGray
                )

                timerState.timeLeft <= Duration.ZERO -> listOf(Color.Red, Color.Red)
                progress <= 0.33f -> listOf(Color.Yellow, Color.Yellow)
                else -> listOf(Color(0xFFFF6E40), MaterialTheme.colorScheme.primary)
            }
        )

        // Timer text + dot
        TimerContent(
            status = timerState.status,
            timeLeft = timerState.timeLeft,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
fun TimerContent(
    status: TimerStatus, timeLeft: Duration, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = when (status) {
                TimerStatus.Idle -> Icons.Outlined.Timer
                TimerStatus.Resumed -> Icons.Outlined.Pause
                TimerStatus.Paused -> Icons.Outlined.PlayArrow
                TimerStatus.Stopped -> Icons.Outlined.Stop
            }, tint = Color.White, contentDescription = null
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = when (status) {
                TimerStatus.Idle -> "Start"
                TimerStatus.Resumed, TimerStatus.Paused -> formatTime(timeLeft.inWholeMilliseconds)
                TimerStatus.Stopped -> "Restart"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}