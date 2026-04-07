package io.github.donald_okara.components.devices

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object DeviceCatalog {

    val Pixel8 = DeviceSpec(
        name = "Pixel 8",
        aspectRatio = 20f / 9f,
        cutout = CutoutSpec(
            type = CutoutType.CENTER_CIRCLE,
            size = 10.dp,
            offsetY = 12.dp
        ),
        bezel = BezelSpec(
            thickness = 14.dp,
            color = Color(0xFF1F1F1F),
            cornerRadius = 32.dp
        ),
        buttons = listOf(
            ButtonSpec(ButtonSide.RIGHT, offset = (-100).dp, length = 30.dp), // Power
            ButtonSpec(ButtonSide.RIGHT, offset = (-40).dp, length = 60.dp)   // Volume
        )
    )

    val GalaxyS26 = DeviceSpec(
        name = "Galaxy S26",
        aspectRatio = 19.5f / 9f,
        bezel = BezelSpec(
            thickness = 10.dp,
            color = Color(0xFF121212),
            cornerRadius = 28.dp
        ),
        cutout = CutoutSpec(
            type = CutoutType.CENTER_CIRCLE,
            size = 8.dp,
            offsetY = 10.dp
        ),
        buttons = listOf(
            ButtonSpec(ButtonSide.RIGHT, offset = (-60).dp, length = 40.dp), // Power
            ButtonSpec(ButtonSide.RIGHT, offset = 40.dp, length = 80.dp)    // Volume
        )
    )

    val IPhone17 = DeviceSpec(
        name = "iPhone 17",
        aspectRatio = 19.5f / 9f,
        bezel = BezelSpec(
            thickness = 12.dp,
            color = Color(0xFF1A1A1A),
            cornerRadius = 36.dp
        ),
        notch = NotchSpec(
            type = NotchType.DETACHED,
            widthFraction = 0.35f,
            height = 30.dp,
            offset = 8.dp,
            cornerRadius = 16.dp
        ),
        buttons = listOf(
            ButtonSpec(ButtonSide.LEFT, offset = (-100).dp, length = 20.dp), // Mute/Action
            ButtonSpec(ButtonSide.LEFT, offset = (-40).dp, length = 45.dp),  // Vol Up
            ButtonSpec(ButtonSide.LEFT, offset = 20.dp, length = 45.dp),    // Vol Down
            ButtonSpec(ButtonSide.RIGHT, offset = (-40).dp, length = 70.dp)  // Side button
        )
    )
}