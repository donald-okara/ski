package ke.don.demos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

/**
 * Turns a URI from the slide into one the current platform's player can open.
 * Each platform provides its own implementation in its source set.
 */
expect suspend fun resolveVideoUriForPlayer(videoUri: String): String

private const val MAX_ZOOM = 3f

/**
 * A video player with play/pause, restart, seek, mute, playback speed, zoom, and fullscreen controls.
 * The controls hide after three seconds of playback and reappear on tap.
 * Pinch to zoom and drag to pan. The zoom button cycles 1x, 1.5x, and 2x.
 *
 * @param videoUri An http(s) URL, or a path the platform can resolve.
 */
@Composable
fun VideoPlayer(
    videoUri: String,
    modifier: Modifier = Modifier
) {
    val playerState = rememberVideoPlayerState()
    var resolvedUri by remember { mutableStateOf<String?>(null) }
    var showControls by remember { mutableStateOf(true) }
    var userInteractionTrigger by remember { mutableStateOf(0) }

    var zoom by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var viewSize by remember { mutableStateOf(IntSize.Zero) }

    val resetHideTimer = {
        showControls = true
        userInteractionTrigger++
    }

    // Keeps the zoomed video covering the frame, so panning cannot reveal the edges.
    fun clampOffset(value: Offset, scale: Float): Offset {
        val maxX = viewSize.width * (scale - 1f) / 2f
        val maxY = viewSize.height * (scale - 1f) / 2f
        return Offset(value.x.coerceIn(-maxX, maxX), value.y.coerceIn(-maxY, maxY))
    }

    LaunchedEffect(Unit) {
        playerState.loop = true
    }

    LaunchedEffect(videoUri) {
        if (videoUri.isNotEmpty()) {
            withContext(Dispatchers.Default) {
                resolvedUri = try {
                    resolveVideoUriForPlayer(videoUri)
                } catch (e: Exception) {
                    videoUri
                }
            }
        }
    }

    LaunchedEffect(resolvedUri) {
        val uri = resolvedUri ?: return@LaunchedEffect
        playerState.openUri(uri)
    }

    // Auto-hide controls after 3 seconds when playing
    LaunchedEffect(showControls, userInteractionTrigger, playerState.isPlaying) {
        if (showControls && playerState.isPlaying) {
            delay(3.seconds)
            showControls = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            playerState.stop()
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { viewSize = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    val newZoom = (zoom * gestureZoom).coerceIn(1f, MAX_ZOOM)
                    zoom = newZoom
                    offset = clampOffset(offset + pan, newZoom)
                }
            }
    ) {
        VideoPlayerSurface(
            playerState = playerState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            overlay = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showControls = !showControls
                            if (showControls) {
                                userInteractionTrigger++
                            }
                        }
                        .padding(8.dp)
                ) {
                    AnimatedVisibility(
                        visible = showControls,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { 20 },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300)) { 20 },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Slider(
                                    value = playerState.sliderPos,
                                    onValueChange = {
                                        resetHideTimer()
                                        playerState.seekStart(it)
                                    },
                                    onValueChangeFinished = {
                                        resetHideTimer()
                                        playerState.seekFinished()
                                    },
                                    valueRange = 0f..1000f,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                resetHideTimer()
                                                if (playerState.isPlaying) {
                                                    playerState.pause()
                                                } else {
                                                    playerState.play()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                                tint = Color.White
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                resetHideTimer()
                                                playerState.restart()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Replay,
                                                contentDescription = "Restart",
                                                tint = Color.White
                                            )
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        var isMuted by remember { mutableStateOf(false) }
                                        var previousVolume by remember { mutableStateOf(1f) }

                                        IconButton(
                                            onClick = {
                                                resetHideTimer()
                                                if (isMuted) {
                                                    playerState.volume = if (previousVolume > 0f) previousVolume else 1f
                                                    isMuted = false
                                                } else {
                                                    previousVolume = playerState.volume
                                                    playerState.volume = 0f
                                                    isMuted = true
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isMuted || playerState.volume == 0f) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Volume",
                                                tint = Color.White
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                resetHideTimer()
                                                val nextZoom = when (zoom) {
                                                    1f -> 1.5f
                                                    1.5f -> 2f
                                                    else -> 1f
                                                }
                                                zoom = nextZoom
                                                offset = clampOffset(offset, nextZoom)
                                            }
                                        ) {
                                            Text(
                                                text = "${(zoom * 10).roundToInt() / 10f}x zoom",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                resetHideTimer()
                                                playerState.playbackSpeed = when (playerState.playbackSpeed) {
                                                    1.0f -> 1.25f
                                                    1.25f -> 1.5f
                                                    1.5f -> 2.0f
                                                    else -> 1.0f
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = "${playerState.playbackSpeed}x",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                resetHideTimer()
                                                playerState.toggleFullscreen()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (playerState.isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                                contentDescription = "Fullscreen",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
