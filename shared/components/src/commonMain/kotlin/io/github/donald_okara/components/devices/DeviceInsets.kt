package io.github.donald_okara.components.devices

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DeviceInsets(
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val start: Dp = 0.dp,
    val end: Dp = 0.dp
)

val LocalDeviceInsets = staticCompositionLocalOf {
    DeviceInsets()
}

fun Modifier.deviceSafePadding(): Modifier = composed {
    val insets = LocalDeviceInsets.current
    this.padding(
        top = insets.top,
        bottom = insets.bottom,
        start = insets.start,
        end = insets.end
    )
}