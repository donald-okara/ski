package io.github.donald_okara.components.devices

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DeviceSpec(
    val name: String,
    val aspectRatio: Float = 19.5f / 9f,
    val orientation: DeviceOrientation = DeviceOrientation.PORTRAIT,
    val bezel: BezelSpec = BezelSpec(),
    val notch: NotchSpec = NotchSpec(),
    val screenColor: Color = Color.White,
    val cutout: CutoutSpec = CutoutSpec(),
    val screenCornerRadius: Dp = 24.dp,
    val buttons: List<ButtonSpec> = emptyList(),
    val isFoldable: Boolean = false,
    val openedAspectRatio: Float = 1.2f // Aspect ratio of the device when opened
) {
    /**
     * The raw size of the logical top inset (notch or cutout).
     */
    val rawTopInset: Dp
        get() = when {
            notch.type == NotchType.ATTACHED -> notch.height
            notch.type == NotchType.DETACHED -> notch.height + notch.offset
            cutout.type != CutoutType.NONE -> cutout.size + cutout.offsetY
            else -> 0.dp
        }

    fun calculateInsets(): DeviceInsets {
        val inset = rawTopInset
        return when (orientation) {
            DeviceOrientation.PORTRAIT, DeviceOrientation.HALF_OPENED -> {
                DeviceInsets(top = inset)
            }
            DeviceOrientation.LANDSCAPE -> {
                // When rotated -90deg (Clockwise), logical top moves to visual Right (End)
                DeviceInsets(end = inset)
            }
        }
    }
}

enum class DeviceOrientation {
    PORTRAIT, LANDSCAPE, HALF_OPENED
}

enum class NotchType {
    NONE,
    ATTACHED,
    DETACHED
}

data class NotchSpec(
    val type: NotchType = NotchType.NONE,
    val widthFraction: Float = 0.4f,
    val height: Dp = 28.dp,
    val offset: Dp = 8.dp,
    val cornerRadius: Dp = 12.dp,
    val color: Color = Color.Black
)

data class BezelSpec(
    val thickness: Dp = 12.dp,
    val color: Color = Color.Black,
    val cornerRadius: Dp = 32.dp
)

enum class CutoutType {
    NONE,
    CENTER_CIRCLE,
    PILL
}

data class CutoutSpec(
    val type: CutoutType = CutoutType.NONE,
    val size: Dp = 12.dp,
    val offsetY: Dp = 10.dp
)

data class ButtonSpec(
    val side: ButtonSide,
    val offset: Dp,
    val length: Dp,
    val thickness: Dp = 3.dp,
    val color: Color = Color.DarkGray
)

enum class ButtonSide {
    LEFT, RIGHT, TOP, BOTTOM
}