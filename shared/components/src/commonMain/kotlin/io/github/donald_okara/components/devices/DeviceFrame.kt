package io.github.donald_okara.components.devices

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

@Composable
fun DeviceFrame(
    spec: DeviceSpec,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isPortrait = spec.orientation == DeviceOrientation.PORTRAIT
    
    // The base ratio is always Portrait (e.g., 9/19.5)
    val baseRatio = 1f / spec.aspectRatio
    
    // The visual ratio changes based on orientation to take up correct layout space
    val targetVisualRatio = if (isPortrait) baseRatio else spec.aspectRatio
    val visualRatio by animateFloatAsState(
        targetValue = targetVisualRatio,
        animationSpec = tween(durationMillis = 500)
    )

    val rotation by animateFloatAsState(
        targetValue = if (isPortrait) 0f else -90f,
        animationSpec = tween(durationMillis = 500)
    )

    LookaheadScope {
        BoxWithConstraints(
            modifier = modifier
                .padding(horizontal = 48.dp)
                .aspectRatio(visualRatio)
        ) {
            // Adaptive scaling: reference phone width is ~360dp
            // In Landscape, the phone's 'width' is the container's height.
            val phoneWidth = if (isPortrait) maxWidth else maxHeight
            val scalingFactor = (phoneWidth / 360.dp).coerceAtLeast(0.1f)

            // Animate insets to match rotation and scale
            val baseInsets = spec.calculateInsets()
            val animatedInsets = DeviceInsets(
                top = animateDpAsState(baseInsets.top * scalingFactor, tween(500)).value,
                bottom = animateDpAsState(baseInsets.bottom * scalingFactor, tween(500)).value,
                start = animateDpAsState(baseInsets.start * scalingFactor, tween(500)).value,
                end = animateDpAsState(baseInsets.end * scalingFactor, tween(500)).value
            )

            // Chassis Box
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .rotate(rotation)
                    .then(
                        // When Landscape, we need to swap constraints so the Portrait chassis
                        // fills the Landscape outer container before it is rotated.
                        if (isPortrait) Modifier.fillMaxSize()
                        else Modifier.layout { measurable, constraints ->
                            val placeable = measurable.measure(
                                constraints.copy(
                                    minWidth = constraints.maxHeight,
                                    maxWidth = constraints.maxHeight,
                                    minHeight = constraints.maxWidth,
                                    maxHeight = constraints.maxWidth
                                )
                            )
                            layout(constraints.maxWidth, constraints.maxHeight) {
                                placeable.placeRelative(
                                    (constraints.maxWidth - placeable.width) / 2,
                                    (constraints.maxHeight - placeable.height) / 2
                                )
                            }
                        }
                    )
            ) {
                // External Buttons
                spec.buttons.forEach { button ->
                    DeviceButton(button, scalingFactor)
                }

                // Main Body (Chassis)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 12.dp * scalingFactor,
                            shape = RoundedCornerShape(spec.bezel.cornerRadius * scalingFactor)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    spec.bezel.color,
                                    spec.bezel.color.copy(alpha = 0.85f),
                                    spec.bezel.color
                                )
                            ),
                            shape = RoundedCornerShape(spec.bezel.cornerRadius * scalingFactor)
                        )
                        .border(
                            width = 1.dp * scalingFactor,
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(spec.bezel.cornerRadius * scalingFactor)
                        )
                        .padding(spec.bezel.thickness * scalingFactor)
                ) {

                    // Screen Area
                    CompositionLocalProvider(
                        LocalDeviceInsets provides animatedInsets
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(spec.screenCornerRadius * scalingFactor))
                                .background(spec.screenColor)
                                .border(
                                    width = 1.dp * scalingFactor,
                                    color = Color.Black.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(spec.screenCornerRadius * scalingFactor)
                                )
                        ) {
                            // Counter-rotate and re-layout the content to fill the screen
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(-rotation)
                                    .then(
                                        if (!isPortrait) Modifier.layout { measurable, constraints ->
                                            val placeable = measurable.measure(
                                                constraints.copy(
                                                    minWidth = constraints.maxHeight,
                                                    maxWidth = constraints.maxHeight,
                                                    minHeight = constraints.maxWidth,
                                                    maxHeight = constraints.maxWidth
                                                )
                                            )
                                            layout(constraints.maxWidth, constraints.maxHeight) {
                                                placeable.placeRelative(
                                                    (constraints.maxWidth - placeable.width) / 2,
                                                    (constraints.maxHeight - placeable.height) / 2
                                                )
                                            }
                                        } else Modifier
                                    )
                            ) {
                                content()
                            }
                        }
                    }

                    // Notch & Cutout
                    if (spec.notch.type != NotchType.NONE) {
                        Notch(spec.notch, scalingFactor)
                    }
                    Cutout(spec.cutout, scalingFactor)
                }
            }
        }
    }
}

@Composable
private fun BoxScope.DeviceButton(spec: ButtonSpec, scale: Float) {
    val alignment = when (spec.side) {
        ButtonSide.LEFT -> Alignment.CenterStart
        ButtonSide.RIGHT -> Alignment.CenterEnd
        ButtonSide.TOP -> Alignment.TopCenter
        ButtonSide.BOTTOM -> Alignment.BottomCenter
    }

    val thickness = spec.thickness * scale
    val length = spec.length * scale
    val offset = spec.offset * scale

    val modifier = when (spec.side) {
        ButtonSide.LEFT, ButtonSide.RIGHT -> {
            Modifier
                .align(alignment)
                .offset(
                    x = if (spec.side == ButtonSide.LEFT) (-thickness) else thickness,
                    y = offset
                )
                .width(thickness)
                .height(length)
        }
        ButtonSide.TOP, ButtonSide.BOTTOM -> {
            Modifier
                .align(alignment)
                .offset(
                    x = offset,
                    y = if (spec.side == ButtonSide.TOP) (-thickness) else thickness
                )
                .width(length)
                .height(thickness)
        }
    }

    Box(
        modifier = modifier
            .background(
                color = spec.color,
                shape = RoundedCornerShape(2.dp * scale)
            )
    )
}

@Composable
private fun BoxScope.Notch(spec: NotchSpec, scale: Float) {
    val topOffset = (when (spec.type) {
        NotchType.ATTACHED -> 0.dp
        NotchType.DETACHED -> spec.offset
        else -> 0.dp
    }) * scale

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = topOffset)
            .fillMaxWidth(spec.widthFraction)
            .height(spec.height * scale)
            .background(
                color = spec.color,
                shape = RoundedCornerShape(spec.cornerRadius * scale)
            )
    ) {
        // Subtle speaker grill
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.35f)
                .height(3.dp * scale)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
    }
}

@Composable
private fun BoxScope.Cutout(spec: CutoutSpec, scale: Float) {
    if (spec.type == CutoutType.NONE) return

    val size = spec.size * scale
    val offsetY = spec.offsetY * scale

    when (spec.type) {
        CutoutType.CENTER_CIRCLE -> {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = offsetY)
                    .size(size)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1A1A1A), Color.Black)
                        ),
                        shape = CircleShape
                    )
                    .border(0.5.dp * scale, Color.White.copy(alpha = 0.1f), CircleShape)
            )
        }

        CutoutType.PILL -> {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = offsetY)
                    .height(size)
                    .fillMaxWidth(0.18f)
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(percent = 50)
                    )
            )
        }
    }
}
