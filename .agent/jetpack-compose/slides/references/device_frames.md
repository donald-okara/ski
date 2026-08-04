# Device Frames

Device frames allow you to showcase content within a realistic hardware chassis (e.g., Pixel 8, iPhone 17).

## DeviceCatalog

Contains pre-defined hardware specifications for various devices.

- `DeviceCatalog.Pixel8`
- `DeviceCatalog.GalaxyS26`
- `DeviceCatalog.IPhone17`
- `DeviceCatalog.PixelFold`

## DeviceFrame

Renders content within a specific device chassis.

```kotlin
DeviceFrame(
    spec = DeviceCatalog.Pixel8.copy(orientation = DeviceOrientation.PORTRAIT),
    modifier = Modifier.size(400.dp)
) {
    // Content to display on the device screen
    Image(Res.drawable.app_screenshot, contentDescription = null)
}
```

### Parameters
- `spec: DeviceSpec`: The specification of the device (from `DeviceCatalog`). You can modify its `orientation`.
- `modifier: Modifier`: Applied to the device frame container.
- `content: @Composable BoxScope.() -> Unit`: The content to be rendered on the device's screen.

## DeviceOrientation

- `DeviceOrientation.PORTRAIT`: Standard upright orientation.
- `DeviceOrientation.LANDSCAPE`: Rotated 90 degrees.
- `DeviceOrientation.HALF_OPENED`: For foldable devices (like Pixel Fold).

## DeviceInsets

The `DeviceFrame` provides `LocalDeviceInsets` via `CompositionLocalProvider`. Use this to handle safe areas (notches, cutouts, rounded corners) on the device screen.

```kotlin
val insets = LocalDeviceInsets.current
Box(modifier = Modifier.padding(
    top = insets.top,
    bottom = insets.bottom,
    start = insets.start,
    end = insets.end
)) {
    // Content safe from notches/cutouts
}
```
