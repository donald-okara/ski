# Frames

Frames provide the structural wrapper for slide content, often including space for headers and footers.

## FrameBuilder

Use `FrameBuilder` to configure and build slide frames.

```kotlin
val frame = FrameBuilder()
    .setCurve(Values.cornerRadius)
    .setOpacity(Values.FRAME_OPACITY)
    .setFrame { snake } // or { basic }
    .build()
```

### Methods
- `setCurve(curve: Dp)`: Sets the corner radius.
- `setOpacity(opacity: Float)`: Sets the background opacity (0.0 to 1.0).
- `setFrame(selector: @Composable SkiFrames.() -> SkiFrameFactory)`: Selects the frame type (`snake` or `basic`).
- `build()`: Returns a `SkiFrame` instance.

## SkiFrame Types

### SnakeFrame
A frame with dynamic, animated "snake" borders that wrap around the header and footer.
- **Factory**: `SkiFrames.snake`
- **Behavior**: Automatically adjusts the border to accommodate header and footer content.

### BasicFrame
A standard rectangular frame with rounded corners.
- **Factory**: `SkiFrames.basic`
- **Behavior**: Simple container with optional header and footer.

## Usage in Slides

Frames are used via the `Render` method, which provides a `BoxScope` for the content.

```kotlin
frame.Render(
    modifier = Modifier.padding(16.dp),
    header = { Text("Slide Title", style = MaterialTheme.typography.headlineMedium) },
    footer = { Text("Slide 1", style = MaterialTheme.typography.labelSmall) }
) {
    // Content goes here (BoxScope)
    Text("Main Content", modifier = Modifier.align(Alignment.Center))
}
```

### Render Parameters
- `modifier: Modifier`: Applied to the outer container.
- `header: (@Composable () -> Unit)?`: Optional header content.
- `footer: (@Composable () -> Unit)?`: Optional footer content.
- `content: @Composable BoxScope.() -> Unit`: The main content of the slide.
